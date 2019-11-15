package com.thorinhood.dbtg.controllers;

import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.models.filters.Filter;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import net.sf.jsefa.Deserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/students")
public class StudentsController {

    @Autowired
    private Deserializer studentsCsvDeserializer;

    @Autowired
    private StudentsRepository studentsRepository;

    @PostMapping("/list")
    public ResponseEntity addStudents(@RequestParam("file") MultipartFile multipart) {
        try {
            studentsCsvDeserializer.open(new InputStreamReader(multipart.getInputStream()));
            while (studentsCsvDeserializer.hasNext()) {
                Student student = studentsCsvDeserializer.next();
                studentsRepository.save(student);
            }
            studentsCsvDeserializer.close(true);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteStudents(@RequestParam List<Long> ids) {
        return ResponseEntity.ok().body(ids.stream()
                .map(this::deleteOneStudent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    @PostMapping("/edit")
    public ResponseEntity editStudent(@RequestBody Student student) {
        if (studentsRepository.findById(student.getTelegramId()).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            studentsRepository.save(student);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity addStudent(@RequestBody Student student) {
        if (studentsRepository.findById(student.getTelegramId()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            studentsRepository.save(student);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteStudent(@RequestParam("telegramId") Long telegramId) {
        Optional<Student> student = deleteOneStudent(telegramId);
        return student.isPresent() ?
                ResponseEntity.ok().body(student.get()) :
                ResponseEntity.badRequest().body("Not found.");
    }

    @GetMapping
    public String getStudents(@RequestParam(value = "lastname", required = false) String lastname,
                                    @RequestParam(value = "group", required = false) String group,
                                    @RequestParam(value = "subgroup", required = false) Integer subgroup,
                                    @RequestParam(value = "firstname", required = false) String firstname,
                                    @RequestParam(value = "email", required = false) String email,
                                    Map<String, Object> model) {
        Filter<Student> filter = new Filter<Student>()
                .andIfNotNull("lastName", lastname, Filter::contains)
                .andIfNotNull("firstName", firstname, Filter::contains)
                .andIfNotNull("group", group, Filter::contains)
                .andIfNotNull("subGroup", subgroup, Filter::eq)
                .andIfNotNull("email", email, Filter::contains);
        List<Student> students = studentsRepository.findAll(filter.getFilter());
        model.put("students", students);
        model.put("lastname", lastname);
        model.put("group", group);
        model.put("subgroup", subgroup);
        model.put("firstname", firstname);
        model.put("email", email);
        return "students";
    }

    private Optional<Student> deleteOneStudent(Long telegramId) {
        Optional<Student> student = studentsRepository.findById(telegramId);
        if (student.isPresent()) {
            studentsRepository.delete(student.get());
            return student;
        } else {
            return Optional.empty();
        }
    }

}
