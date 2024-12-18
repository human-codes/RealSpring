package com.democha.realspring.entity;

import com.democha.realspring.entity.student.Student;
import com.democha.realspring.entity.student.StudentRepository;
import com.democha.realspring.entity.task.Task;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping( "/task")
@RequiredArgsConstructor
public class TaskController {


    private final TaskRepository taskRepository;
    private final StudentRepository studentRepository;


    @GetMapping("/getTasks")
    public String getTasks(Model model, HttpServletRequest request) {
        System.out.println("salom");
        Student student = (Student) request.getSession().getAttribute("currentStudent");
        Optional<Student> byId = studentRepository.findById(student.getId());
        Student student1 = byId.get();
        System.out.println(student1.getUsername());
        List<Task> all = taskRepository.findAll();
        List<Task> tasks = all.stream()
                .filter(task -> task.getStudent().getId().equals(student1.getId())).collect(Collectors.toList());
        model.addAttribute("tasks", tasks);
        for (Task task : tasks) {
            System.out.println(task);
        }
        return "/home";
    }


    @PostMapping("/add-task")
    public String addTask(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("priority") String priority,
            @RequestParam("deadline") LocalDateTime deadline,
            HttpServletRequest request
    ) {

        Student student=(Student)request.getSession().getAttribute("currentStudent");
        Optional<Student> byId = studentRepository.findById(student.getId());
        Student student1 = byId.get();
        System.out.println(student1.getUsername());
        if (deadline.isBefore(LocalDateTime.now())) {
            return "redirect:/task/new-task";
        }
        System.out.println(student1.getUsername());
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDeadline(deadline);
        task.setStudent(student1);
        student1.getTasks().add(task);
        taskRepository.save(task);
        studentRepository.save(student1);

        return "redirect:/task/getTasks";
    }

    @PostMapping("moveTask")
    public String moveTask(@RequestParam(name = "id") Long id)
    {
        Task task=taskRepository.getTaskById(id);
        String status = task.getStatus();
        if (status.equals("INPROGRESS")) {
            task.setStatus("COMPLETED");
            taskRepository.save(task);
            return "redirect:/task/getTasks";
        }
        else{
            task.setStatus("INPROGRESS");
            taskRepository.save(task);
            return "redirect:/task/getTasks";
        }
    }



    @GetMapping("new-task")
    public String newTask() {
        return "newTask";
    }
}
