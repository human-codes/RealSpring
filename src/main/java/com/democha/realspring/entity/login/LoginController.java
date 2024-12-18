package com.democha.realspring.entity.login;

import com.democha.realspring.entity.student.Student;
import com.democha.realspring.entity.student.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final StudentRepository studentRepository;


    @GetMapping("checkUser")
    public String checkUser(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        Optional<Student> student = studentRepository.findByUsernameAndPassword(username,password);
        if (student.isPresent()) {
                HttpSession session = request.getSession();
                session.setAttribute("currentStudent", student.get());
                return "redirect:task/getTasks";
        }
        return "login/login-page";
    }

    @PostMapping("logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login/login-page";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @PostMapping("registrate")
    public String registrate(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        if (!password.equals(confirmPassword)) {
            return "redirect:/login/registration";
        }
        Optional<Student> optionalStudent = studentRepository.findByUsername(username);
        if (optionalStudent.isEmpty()) {
                Student student = new Student();
                student.setUsername(username);
                student.setPassword(password);
                studentRepository.save(student);
            request.getSession().setAttribute("currentStudent",student);
            return "redirect:/login";
        }
        return "redirect:/registration";

    }

    @GetMapping("/registration")
    public String registration() {
        return "login/registration";
    }
}