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
    private final Map<String, Map<String, Boolean>> roomToRaisedHands = new ConcurrentHashMap<>();
    private final Map<String, List<Comment>> roomToComments = new ConcurrentHashMap<>(); // Lưu trữ bình luận

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

    private static class Comment {
        String username;
        String content;
        long timestamp;

        Comment(String username, String content, long timestamp) {
            this.username = username;
            this.content = content;
            this.timestamp = timestamp;
        }

        String toJson() {
            return "{\"username\": \"" + username + "\", \"content\": \"" + content + "\", \"timestamp\": " + timestamp + "}";
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
                    if (parts[0].equals("roomId")) roomId = parts[1];
                    else if (parts[0].equals("username")) username = parts[1];
                    else if (parts[0].equals("isCreator")) isCreator = Boolean.parseBoolean(parts[1]);
                }
            }
        }
        if (roomId == null || username == null) {
            session.close();
            System.err.println("Invalid roomId or username, closing connection: " + session.getId());
            return;
        }

        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(session.getId(), session);
        sessionToRoom.put(session.getId(), roomId);
        sessionToUsername.put(session.getId(), username);
        usernameToSession.put(username, session.getId());
        roomToProducts.computeIfAbsent(roomId, k -> new ArrayList<>());
        roomToRaisedHands.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
        roomToComments.computeIfAbsent(roomId, k -> new ArrayList<>());

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

        System.out.println("User " + username + " joined room " + roomId + " with session: " + session.getId());

        String joinMessage = "{\"id\": \"" + username + "\", \"type\": \"join\", \"username\": \"" + username + "\", \"roomId\": \"" + roomId + "\"}";
        broadcast(roomId, new TextMessage(joinMessage), session.getId());

        String existingUsers = "{\"type\": \"existingUsers\", \"users\": [" + String.join(",", rooms.get(roomId).keySet().stream()
                .filter(id -> !id.equals(session.getId())).map(sessionToUsername::get).map(u -> "\"" + u + "\"").toArray(String[]::new)) + "]}";
        session.sendMessage(new TextMessage(existingUsers));

        String streamerId = roomToStreamer.get(roomId);
        if (streamerId != null) {
            String roomInfoMessage = "{\"type\": \"roomInfo\", \"streamerId\": \"" + streamerId + "\", \"roomId\": \"" + roomId + "\"}";
            session.sendMessage(new TextMessage(roomInfoMessage));
            broadcast(roomId, new TextMessage(roomInfoMessage), session.getId());
        }

        List<Product> products = roomToProducts.getOrDefault(roomId, new ArrayList<>());
        String productsMessage = "{\"type\": \"products\", \"products\": [" + String.join(",", products.stream().map(Product::toJson).toArray(String[]::new)) + "]}";
        session.sendMessage(new TextMessage(productsMessage));

        Product pinnedProduct = roomToPinnedProduct.get(roomId);
        if (pinnedProduct != null) {
            String pinnedProductMessage = "{\"type\": \"pinnedProduct\", \"product\": " + pinnedProduct.toJson() + "}";
            session.sendMessage(new TextMessage(pinnedProductMessage));
        }

        Map<String, Boolean> raisedHands = roomToRaisedHands.get(roomId);
        String raisedHandsMessage = "{\"type\": \"raisedHands\", \"raisedHands\": " + new JSONObject(raisedHands).toString() + "}";
        session.sendMessage(new TextMessage(raisedHandsMessage));

        // Gửi danh sách bình luận hiện tại khi người dùng mới tham gia
        List<Comment> comments = roomToComments.getOrDefault(roomId, new ArrayList<>());
        String commentsMessage = "{\"type\": \"comments\", \"comments\": [" + String.join(",", comments.stream().map(Comment::toJson).toArray(String[]::new)) + "]}";
        session.sendMessage(new TextMessage(commentsMessage));
        System.out.println("Sent comments list to " + username + " in room " + roomId + ": " + commentsMessage);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = sessionToRoom.get(session.getId());
        if (roomId == null) {
            System.err.println("Room ID not found for session " + session.getId());
            return;
        }

        System.out.println("Message from " + session.getId() + " in room " + roomId + ": " + message.getPayload());
        JSONObject json = new JSONObject(message.getPayload());
        String type = json.optString("type");

        if ("status".equals(type)) {
            broadcast(roomId, message, session.getId());
        } else if ("getRoomInfo".equals(type)) {
            String streamerId = roomToStreamer.get(roomId);
            if (streamerId != null) {
                String roomInfoMessage = "{\"type\": \"roomInfo\", \"streamerId\": \"" + streamerId + "\", \"roomId\": \"" + roomId + "\"}";
                session.sendMessage(new TextMessage(roomInfoMessage));
                broadcast(roomId, new TextMessage(roomInfoMessage), session.getId());
                System.out.println("Sent roomInfo to " + sessionToUsername.get(session.getId()) + " in room " + roomId);
            }
        } else if ("addProduct".equals(type)) {
            String username = sessionToUsername.get(session.getId());
            if (!username.equals(roomToStreamer.get(roomId))) {
                System.err.println("User " + username + " is not the Streamer in room " + roomId + ", cannot add product");
                return;
            }
            Product product = new Product(json.optString("id"), json.optString("name"), json.optDouble("price"), json.optString("imageUrl"), json.optString("link"));
            roomToProducts.get(roomId).add(product);
            String productsMessage = "{\"type\": \"products\", \"products\": [" + String.join(",", roomToProducts.get(roomId).stream().map(Product::toJson).toArray(String[]::new)) + "]}";
            broadcast(roomId, new TextMessage(productsMessage), null);
            System.out.println("Broadcast updated products list to room " + roomId);
        } else if ("pinProduct".equals(type)) {
            String username = sessionToUsername.get(session.getId());
            if (!username.equals(roomToStreamer.get(roomId))) {
                System.err.println("User " + username + " is not the Streamer in room " + roomId + ", cannot pin product");
                return;
            }
            String productId = json.optString("productId");
            Product productToPin = roomToProducts.get(roomId).stream().filter(p -> p.id.equals(productId)).findFirst().orElse(null);
            if (productToPin != null) {
                roomToPinnedProduct.put(roomId, productToPin);
                String pinnedProductMessage = "{\"type\": \"pinnedProduct\", \"product\": " + productToPin.toJson() + "}";
                broadcast(roomId, new TextMessage(pinnedProductMessage), null);
                System.out.println("Pinned product and broadcast to room " + roomId);
            }
        } else if ("unpinProduct".equals(type)) {
            String username = sessionToUsername.get(session.getId());
            if (!username.equals(roomToStreamer.get(roomId))) {
                System.err.println("User " + username + " is not the Streamer in room " + roomId + ", cannot unpin product");
                return;
            }
            roomToPinnedProduct.remove(roomId);
            broadcast(roomId, new TextMessage("{\"type\": \"unpinProduct\"}"), null);
            System.out.println("Unpinned product and broadcast to room " + roomId);
        } else if ("raiseHand".equals(type)) {
            String username = sessionToUsername.get(session.getId());
            boolean raised = json.optBoolean("raised");
            Map<String, Boolean> raisedHands = roomToRaisedHands.get(roomId);
            if (raised) raisedHands.put(username, true);
            else raisedHands.remove(username);
            String raisedHandsMessage = "{\"type\": \"raisedHands\", \"raisedHands\": " + new JSONObject(raisedHands).toString() + "}";
            broadcast(roomId, new TextMessage(raisedHandsMessage), null);
            System.out.println("Broadcast raised hands update to room " + roomId);
        } else if ("comment".equals(type)) {
            String username = sessionToUsername.get(session.getId());
            String content = json.optString("content");
            long timestamp = json.optLong("timestamp");
            if (content != null && !content.trim().isEmpty()) {
                Comment comment = new Comment(username, content, timestamp);
                roomToComments.get(roomId).add(comment);
                String commentMessage = "{\"type\": \"comment\", \"username\": \"" + username + "\", \"content\": \"" + content + "\", \"timestamp\": " + timestamp + "}";
                broadcast(roomId, new TextMessage(commentMessage), null);
                System.out.println("Broadcast comment to room " + roomId + ": " + commentMessage);
            }
        } else if (json.has("sdp") || json.has("candidate")) {
            String target = json.optString("target");
            if (target == null || target.isEmpty()) {
                System.err.println("Invalid target in SDP/ICE candidate message: " + message.getPayload());
                return;
            }
            String targetSessionId = usernameToSession.get(target);
            if (targetSessionId != null && rooms.get(roomId).containsKey(targetSessionId)) {
                WebSocketSession targetSession = rooms.get(roomId).get(targetSessionId);
                if (targetSession.isOpen()) {
                    targetSession.sendMessage(message);
                    System.out.println("Sent to target " + target + " (session: " + targetSessionId + ")");
                }
            } else {
                System.err.println("Target " + target + " not found in room " + roomId);
            }
        } else {
            broadcast(roomId, message, session.getId());
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
                    roomToRaisedHands.remove(roomId);
                    roomToComments.remove(roomId);
                    System.out.println("Room " + roomId + " is empty and removed");
                } else {
                    if (username.equals(roomToStreamer.get(roomId))) {
                        String newStreamer = roomSessions.keySet().stream().map(sessionToUsername::get).findFirst().orElse(null);
                        if (newStreamer != null) {
                            roomToStreamer.put(roomId, newStreamer);
                            String roomInfoMessage = "{\"type\": \"roomInfo\", \"streamerId\": \"" + newStreamer + "\", \"roomId\": \"" + roomId + "\"}";
                            broadcast(roomId, new TextMessage(roomInfoMessage), null);
                            System.out.println("New Streamer assigned: " + newStreamer + " for room " + roomId);
                        }
                    }
                    Map<String, Boolean> raisedHands = roomToRaisedHands.get(roomId);
                    if (raisedHands.containsKey(username)) {
                        raisedHands.remove(username);
                        String raisedHandsMessage = "{\"type\": \"raisedHands\", \"raisedHands\": " + new JSONObject(raisedHands).toString() + "}";
                        broadcast(roomId, new TextMessage(raisedHandsMessage), null);
                        System.out.println("Updated raised hands after " + username + " left room " + roomId);
                    }
                }
                String leaveMessage = "{\"id\": \"" + username + "\", \"type\": \"leave\", \"roomId\": \"" + roomId + "\"}";
                broadcast(roomId, new TextMessage(leaveMessage), session.getId());
                System.out.println("User " + username + " left room " + roomId);
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
                    }
                }
            }
        }
    }
}