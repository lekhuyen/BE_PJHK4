package fpt.aptech.server_be.entities;

import jakarta.persistence.Entity;
import lombok.*;



@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypingStatus {
    private String userId;
    private boolean typing;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
