package br.com.juhllian.todolist.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.juhllian.todolist.filter.utils.Utils;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");

        if (taskModel == null) { // Corrigido de task para taskModel
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada"); // Removido body: e corrigido para String
        }

        if (idUser instanceof UUID) {
            taskModel.setIdUser((UUID) idUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de usuário inválido.");
        }

        var currentDate = LocalDateTime.now();

        if (taskModel.getStartAt() != null && taskModel.getEndAt() != null) {
            if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("A data de início/término deve ser maior que a data atual.");
            }

            if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("A data de início deve ser anterior à data de término.");
            }
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");

        if (idUser instanceof UUID) {
            var tasks = this.taskRepository.findByIdUser((UUID) idUser);
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");

        var task = this.taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        if (!task.getIdUser().equals(idUser)) { // Corrigido para comparar com idUser
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa");
        }

        // Chamada do método Utils
        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }
}
