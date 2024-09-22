package br.com.juhllian.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String title; // Corrigido de tittle para title
    private String description;

    @Column(nullable = false)
    private String priority;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Column(nullable = false)
    private UUID idUser;

    private LocalDateTime createAt;

    public void validateTitle(String title) throws Exception { // Corrigido o método
        if (title.length() > 50) {
            throw new Exception("O campo title deve conter no máximo 50 caracteres"); // Corrigido de trhow para throw
        }
    }
}
