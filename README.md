# Ceng-Online-App

Announcement, Assignment, Comment, Course, Message, Post, StudentUser, TeacherUser, User are classes in OOP manner. 

A Dialog class is created since there exists a dialog option to warn the user when a course gets deleted. Dialog class has a DialogListener included.

Remaining classes are Activity Classes which also includes a layout XML file for them.

Announcement class holds information about an announcement such as subject, sender, date, course information, and a description.

Assignment class holds information about an assignment such as description, deadline, name, course, and an uploads map.

Comment class holds information about a comment such as a sender, the comment, date.

Course class holds information about a course such as a code, grade, lab teacher. name, sections, teacher, term, students array containing enrolled Students, assignments array containing given Assignments, and announcements array containing Announcements.

Message class holds information about a message such as a sender, receiver, date, subject, message, receiver name, and a sender name.

Post class holds information about the post such as date, sender, the post, course, and the comments array.

User class holds information about the user such as name, email, and user type. User class is the superclass of StudentUser and TeacherUser classes. StudentUser class holds information about the student such as GPA, student number, and grade. TeacherUser class holds information about the teacher such as academic rank and phone.

*For data repository purposes Firebase Firestore is used.
