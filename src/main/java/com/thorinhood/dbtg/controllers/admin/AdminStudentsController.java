package com.thorinhood.dbtg.controllers.admin;

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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/admin/students")
public class AdminStudentsController {

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
    public ResponseEntity deleteStudents(@RequestParam List<String> ids) {
        return ResponseEntity.ok().body(ids.stream()
            .map(this::deleteOneStudent)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity addStudent(@RequestBody Student student) {
        try {
            studentsRepository.save(student);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteStudent(@RequestParam("telegramId") String telegramId) {
        Optional<Student> student = deleteOneStudent(telegramId);
        return student.isPresent() ?
            ResponseEntity.ok().body(student.get()) :
            ResponseEntity.badRequest().body("Not found.");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Student>> getStudents(@RequestParam(value = "lastname", required = false) String lastname,
                                                     @RequestParam(value = "group", required = false) String group,
                                                     @RequestParam(value = "subgroup", required = false) Integer subgroup,
                                                     @RequestParam(value = "firstname", required = false) String firstname) {
        Filter<Student> filter = new Filter<Student>()
            .andIfNotNull("lastName", lastname)
            .andIfNotNull("firstName", firstname)
            .andIfNotNull("group", group)
            .andIfNotNull("subGroup", subgroup);
        List<Student> students = studentsRepository.findAll(filter.getFilter());
        return ResponseEntity.ok().body(students);
    }

    private Optional<Student> deleteOneStudent(String telegramId) {
        Optional<Student> student = studentsRepository.findById(telegramId);
        if (student.isPresent()) {
            studentsRepository.delete(student.get());
            return student;
        } else {
            return Optional.empty();
        }
    }

}
