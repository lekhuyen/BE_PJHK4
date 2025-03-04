package fpt.aptech.server_be.ConfigSocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

public class SignalingHandler extends TextWebSocketHandler {
    private final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUsername = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToSession = new ConcurrentHashMap<>();
    private final Map<String, String> roomToStreamer = new ConcurrentHashMap<>();
    private final Map<String, String> roomCreator = new ConcurrentHashMap<>();
    private final Map<String, List<Product>> roomToProducts = new ConcurrentHashMap<>();
    private final Map<String, Product> roomToPinnedProduct = new ConcurrentHashMap<>();

    private static class Product {
        String id;
        String name;
        double price;
        String imageUrl;
        String link;

        Product(String id, String name, double price, String imageUrl, String link) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.link = link;
        }

        String toJson() {
            return "{\"id\": \"" + id + "\", \"name\": \"" + name + "\", \"price\": " + price +
                    ", \"imageUrl\": \"" + imageUrl + "\", \"link\": \"" + link + "\"}";
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String roomId = null;
        String username = null;
        boolean isCreator = false;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] parts = param.split("=");
                if (parts.length > 1) {
                    if (parts[0].equals("roomId")) {
                        roomId = parts[1];
                    } else if (parts[0].equals("username")) {
                        username = parts[1];
                    } else if (parts[0].equals("isCreator")) {
                        isCreator = Boolean.parseBoolean(parts[1]);
                    }
                }
            }
        }
        if (roomId == null || roomId.trim().isEmpty() || username == null || username.trim().isEmpty()) {
            session.close();
            System.err.println("Invalid roomId or username, closing connection: " + session.getId());
            return;
        }

        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        sessionToRoom.put(session.getId(), roomId);
        sessionToUsername.put(session.getId(), username);
        usernameToSession.put(username, session.getId());

        roomToProducts.computeIfAbsent(roomId, k -> new ArrayList<>());

        if (isCreator) {
            roomCreator.putIfAbsent(roomId, username);
            roomToStreamer.put(roomId, username);
            System.out.println("User " + username + " created room " + roomId + " and is the Streamer");
        } else {
            if (!roomToStreamer.containsKey(roomId)) {
                roomToStreamer.put(roomId, username);
                System.out.println("User " + username + " joined room " + roomId + " and is the Streamer (room had no Streamer)");
            }
        }

        System.out.println("User " + username + " joined room " + roomId + " with session: " + session.getId() + ". Streamer of room: " + roomToStreamer.get(roomId));

        String joinMessage = "{\"id\": \"" + username + "\", \"type\": \"join\", \"username\": \"" + username + "\", \"roomId\": \"" + roomId + "\"}";
        broadcast(roomId, new TextMessage(joinMessage), session.getId());

        String existingUsers = "{\"type\": \"existingUsers\", \"users\": [";
        boolean first = true;
        for (String sessionId : rooms.get(roomId).keySet()) {
            if (!sessionId.equals(session.getId())) {
                String existingUsername = sessionToUsername.get(sessionId);
                if (!first) existingUsers += ",";
                existingUsers += "\"" + existingUsername + "\"";
                first = false;
            }
        }
        existingUsers += "]}";
        session.sendMessage(new TextMessage(existingUsers));

        String streamerId = roomToStreamer.get(roomId);
        if (streamerId != null) {
            String roomInfoMessage = "{\"type\": \"roomInfo\", \"streamerId\": \"" + streamerId + "\", \"roomId\": \"" + roomId + "\"}";
            session.sendMessage(new TextMessage(roomInfoMessage));
            broadcast(roomId, new TextMessage(roomInfoMessage), session.getId());
            System.out.println("Sent roomInfo to all users in room " + roomId + ": " + roomInfoMessage);
        } else {
            System.err.println("No Streamer found for room " + roomId + " when user " + username + " joined");
        }

        List<Product> products = roomToProducts.getOrDefault(roomId, new ArrayList<>());
        StringBuilder productsMessage = new StringBuilder("{\"type\": \"products\", \"products\": [");
        first = true;
        for (Product product : products) {
            if (!first) productsMessage.append(",");
            productsMessage.append(product.toJson());
            first = false;
        }
        productsMessage.append("]}");
        session.sendMessage(new TextMessage(productsMessage.toString()));
        System.out.println("Sent products list to " + username + " in room " + roomId + ": " + productsMessage);

        Product pinnedProduct = roomToPinnedProduct.get(roomId);
        if (pinnedProduct != null) {
            String pinnedProductMessage = "{\"type\": \"pinnedProduct\", \"product\": " + pinnedProduct.toJson() + "}";
            session.sendMessage(new TextMessage(pinnedProductMessage));
            System.out.println("Sent pinned product to " + username + " in room " + roomId + ": " + pinnedProductMessage);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = sessionToRoom.get(session.getId());
        if (roomId == null) {
            System.err.println("Room ID not found for session " + session.getId());
            return;
        }

        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        System.out.println("Message from " + session.getId() + " in room " + roomId + ": " + message.getPayload());

        try {
            JSONObject json = new JSONObject(message.getPayload());
            String type = json.optString("type");

            if ("status".equals(type)) {
                broadcast(roomId, message, session.getId());
            } else if ("getRoomInfo".equals(type)) {
                String streamerId = roomToStreamer.get(roomId);
                if (streamerId != null) {
                    String roomInfoMessage = "{\"type\": \"roomInfo\", \"streamerId\": \"" + streamerId + "\", \"roomId\": \"" + roomId + "\"}";
                    session.sendMessage(new TextMessage(roomInfoMessage));
                    System.out.println("Sent roomInfo to " + sessionToUsername.get(session.getId()) + " in room " + roomId + ": " + roomInfoMessage);
                    broadcast(roomId, new TextMessage(roomInfoMessage), session.getId());
                } else {
                    System.err.println("No Streamer found for room " + roomId + " when handling getRoomInfo from " + sessionToUsername.get(session.getId()));
                }
            } else if ("addProduct".equals(type)) {
                String username = sessionToUsername.get(session.getId());
                String streamerId = roomToStreamer.get(roomId);
                if (streamerId == null) {
                    System.err.println("Streamer not found for room " + roomId + ", cannot add product");
                    return;
                }
                if (!username.equals(streamerId)) {
                    System.err.println("User " + username + " is not the Streamer in room " + roomId + ", cannot add product");
                    return;
                }

                List<Product> productList = roomToProducts.get(roomId);
                if (productList == null) {
                    System.err.println("Product list not initialized for room " + roomId);
                    productList = new ArrayList<>();
                    roomToProducts.put(roomId, productList);
                }

                String productId = json.optString("id");
                String productName = json.optString("name");
                double productPrice = json.optDouble("price", 0.0);
                String productImageUrl = json.optString("imageUrl");
                String productLink = json.optString("link");

                Product product = new Product(productId, productName, productPrice, productImageUrl, productLink);
                productList.add(product);
                System.out.println("Added product to room " + roomId + ": " + product.toJson());

                List<Product> products = roomToProducts.get(roomId);
                StringBuilder productsMessage = new StringBuilder("{\"type\": \"products\", \"products\": [");
                boolean first = true;
                for (Product p : products) {
                    if (!first) productsMessage.append(",");
                    productsMessage.append(p.toJson());
                    first = false;
                }
                productsMessage.append("]}");
                broadcast(roomId, new TextMessage(productsMessage.toString()), null);
                System.out.println("Broadcast updated products list to room " + roomId + ": " + productsMessage);
            } else if ("pinProduct".equals(type)) {
                String username = sessionToUsername.get(session.getId());
                String streamerId = roomToStreamer.get(roomId);
                if (streamerId == null) {
                    System.err.println("Streamer not found for room " + roomId + ", cannot pin product");
                    return;
                }
                if (!username.equals(streamerId)) {
                    System.err.println("User " + username + " is not the Streamer in room " + roomId + ", cannot pin product");
                    return;
                }

                String productId = json.optString("productId");
                Product productToPin = null;
                for (Product product : roomToProducts.get(roomId)) {
                    if (product.id.equals(productId)) {
                        productToPin = product;
                        break;
                    }
                }

                if (productToPin != null) {
                    roomToPinnedProduct.put(roomId, productToPin);
                    String pinnedProductMessage = "{\"type\": \"pinnedProduct\", \"product\": " + productToPin.toJson() + "}";
                    broadcast(roomId, new TextMessage(pinnedProductMessage), null);
                    System.out.println("Pinned product and broadcast to room " + roomId + ": " + pinnedProductMessage);
                } else {
                    System.err.println("Product with ID " + productId + " not found in room " + roomId);
                }
            } else if ("unpinProduct".equals(type)) {
                String username = sessionToUsername.get(session.getId());
                String streamerId = roomToStreamer.get(roomId);
                if (streamerId == null) {
                    System.err.println("Streamer not found for room " + roomId + ", cannot unpin product");
                    return;
                }
                if (!username.equals(streamerId)) {
                    System.err.println("User " + username + " is not the Streamer in room " + roomId + ", cannot unpin product");
                    return;
                }

                roomToPinnedProduct.remove(roomId);
                String unpinMessage = "{\"type\": \"unpinProduct\"}";
                broadcast(roomId, new TextMessage(unpinMessage), null);
                System.out.println("Unpinned product and broadcast to room " + roomId + ": " + unpinMessage);
            } else if (json.has("sdp") || json.has("candidate")) {
                String target = json.optString("target");
                if (target == null || target.isEmpty()) {
                    System.err.println("Invalid target in SDP/ICE candidate message: " + message.getPayload());
                    return;
                }
                String targetSessionId = usernameToSession.get(target);
                if (targetSessionId == null || !roomSessions.containsKey(targetSessionId)) {
                    System.err.println("Target " + target + " not found in room " + roomId);
                    return;
                }
                WebSocketSession targetSession = roomSessions.get(targetSessionId);
                if (targetSession.isOpen()) {
                    targetSession.sendMessage(message);
                    System.out.println("Sent to target " + target + " (session: " + targetSessionId + "): " + message.getPayload());
                }
            } else {
                broadcast(roomId, message, session.getId());
            }
        } catch (Exception e) {
            System.err.println("Error parsing message: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = sessionToRoom.remove(session.getId());
        String username = sessionToUsername.remove(session.getId());
        if (roomId != null && username != null) {
            usernameToSession.remove(username);
            Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
            if (roomSessions != null) {
                roomSessions.remove(session.getId());
                if (roomSessions.isEmpty()) {
                    rooms.remove(roomId);
                    roomToStreamer.remove(roomId);
                    roomCreator.remove(roomId);
                    roomToProducts.remove(roomId);
                    roomToPinnedProduct.remove(roomId);
                } else if (username.equals(roomToStreamer.get(roomId))) {
                    String newStreamer = roomSessions.keySet().stream()
                            .map(sessionToUsername::get)
                            .findFirst()
                            .orElse(null);
                    if (newStreamer != null) {
                        roomToStreamer.put(roomId, newStreamer);
                        String roomInfoMessage = "{\"type\": \"roomInfo\", \"streamerId\": \"" + newStreamer + "\", \"roomId\": \"" + roomId + "\"}";
                        broadcast(roomId, new TextMessage(roomInfoMessage), null);
                        System.out.println("Sent roomInfo to all users in room " + roomId + " after Streamer left: " + roomInfoMessage);
                    }
                }
                System.out.println("User " + username + " left room " + roomId + " with session: " + session.getId());

                String leaveMessage = "{\"id\": \"" + username + "\", \"type\": \"leave\", \"roomId\": \"" + roomId + "\"}";
                broadcast(roomId, new TextMessage(leaveMessage), session.getId());
            }
        }
    }

    private void broadcast(String roomId, TextMessage message, String excludeSessionId) {
        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            for (WebSocketSession session : roomSessions.values()) {
                if (excludeSessionId == null || !session.getId().equals(excludeSessionId)) {
                    if (session.isOpen()) {
                        try {
                            session.sendMessage(message);
                            System.out.println("Broadcast to " + sessionToUsername.get(session.getId()) + " (session: " + session.getId() + "): " + message.getPayload());
                        } catch (Exception e) {
                            System.err.println("Failed to broadcast to " + session.getId() + ": " + e.getMessage());
                        }
                    } else {
                        System.err.println("Cannot broadcast to session " + session.getId() + ", session is not open");
                    }
                }
            }
        } else {
            System.err.println("No sessions found for room " + roomId);
        }
    }
}