package ru.ifmo.rain.pevnev.student;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StudentQueryImpl implements StudentQuery {

    private static final Comparator<Student> studentByNameComparator =
            Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getId);

    @Override
    public List<String> getFirstNames(List<Student> list) {
        return getProperty(Student::getFirstName, list);
    }

    @Override
    public List<String> getLastNames(List<Student> list) {
        return getProperty(Student::getLastName, list);
    }

    @Override
    public List<String> getGroups(List<Student> list) {
        return getProperty(Student::getGroup, list);
    }

    private List<String> getProperty(Function<Student, String> property, List<Student> list) {
        return list
                .stream()
                .map(property)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> list) {
        return getProperty((student -> student.getFirstName() + " " + student.getLastName()), list);
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> list) {
        return new TreeSet<>(getFirstNames(list));
    }

    @Override
    public String getMinStudentFirstName(List<Student> list) {
        return list
                .stream()
                .min(Student::compareTo)
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> collection) {
        return sortStudentsBy(Student::compareTo, collection);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> collection) {
        return sortStudentsBy(studentByNameComparator, collection);
    }

    private List<Student> sortStudentsBy(Comparator<Student> cmp, Collection<Student> collection) {
        return collection
                .stream()
                .sorted(cmp)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> collection, String s) {
        return findStudentsBy(Student::getFirstName, collection, s);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> collection, String s) {
        return findStudentsBy(Student::getLastName, collection, s);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> collection, String s) {
        return findStudentsBy(Student::getGroup, collection, s);
    }

    private List<Student> findStudentsBy(
            Function<Student, String> mapper,
            Collection<Student> collection,
            String s) {
        return collection
                .stream()
                .filter((student) -> mapper.apply(student).equals(s))
                .sorted(studentByNameComparator)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> collection, String s) {
        return collection.stream().filter(student -> student.getGroup().equals(s)).collect(
                Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        (name1, name2) -> name1.compareTo(name2) < 0 ? name1 : name2
                ));
    }
}
