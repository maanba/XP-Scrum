package dk.cphbusiness.control;

import contract.EessInterface;
import dk.cphbusiness.model.Assembler;
import dk.cphbusiness.model.Student;
import dk.cphbusiness.model.Subject;
import dk.cphbusiness.model.Teacher;
import dto.DTOStudent;
import dto.DTOSubject;
import dto.DTOTeacher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Ejer
 */


@Stateless
public class EESSManagerBean implements EessInterface
{

    @PersistenceContext(unitName = "EESSBackendPU")
    private EntityManager em;

    @Override
    public ArrayList<DTOStudent> getStudents()
    {
        Query query = em.createNamedQuery("Student.findAll");
        List<Student> students = query.getResultList();
        ArrayList<DTOStudent> dtoStudents = new ArrayList<>();
        for (int i = 0; i < students.size(); i++)
        {
            dtoStudents.add(Assembler.StudentObjectToDTOStudent(students.get(i)));
        }
        return dtoStudents;
    }

    @Override
    public DTOStudent getStudent(int id)
    {
        Student student = em.find(Student.class, id);
        return Assembler.StudentObjectToDTOStudent(student);
    }

    @Override
    public ArrayList<DTOTeacher> getTeachers()
    {
        Query query = em.createNamedQuery("Teacher.findAll");
        List<Teacher> teachers = query.getResultList();
        ArrayList<DTOTeacher> dtoTeachers = new ArrayList<>();
        for (int i = 0; i < teachers.size(); i++)
        {
            dtoTeachers.add(Assembler.TeacherObjectToDTOTeacher(teachers.get(i)));
        }
        return dtoTeachers;
    }

    @Override
    public DTOTeacher getTeacher(int id)
    {
        Teacher teacher = em.find(Teacher.class, id);
        return Assembler.TeacherObjectToDTOTeacher(teacher);
    }

    @Override
    public ArrayList<DTOSubject> getSubjects()
    {
        Query query = em.createNamedQuery("Subject.findAll");
        Collection subject = query.getResultList();
        Collection<DTOSubject> subjects = Assembler.CreateSubjects(subject);
        ArrayList<DTOSubject> subs = new ArrayList();
        for (DTOSubject ret : subjects)
        {
            subs.add(ret);
        }
        return subs;
    }

    @Override
    public DTOSubject getSubject(int id)
    {
        Subject subject = em.find(Subject.class, id);
        return Assembler.SubjectObjectToDTOSubject(subject);
    }

    @Override
    public void setPool(int id, String pool)
    {
        Subject subject = em.find(Subject.class, id);
        subject.setPool(pool);
        persist(subject);
    }

    @Override
    public void setSubjectTeacher(int teacherId, int subjectId)
    {
        Teacher teacher = em.find(Teacher.class, teacherId);
        Subject subject = em.find(Subject.class, subjectId);
        Query q = em.createQuery("INSERT INTO subject_teacher VALUES (?,?)");
        q.setParameter(0, teacher);
        q.setParameter(1, subject);
        persist(q);
    }

    @Override
    public boolean validateLogin(String username, String password)
    {
        return !username.equals("") && !password.equals("");
    }

    public
            void persist(Object object)
    {
        em.persist(object);
    }

    @Override
    public void setPriorities(DTOStudent dtoStudent)
    {
        Student student = em.find(Student.class, dtoStudent.getId());
        DTOSubject[] firstPriorities = dtoStudent.getFirstPriorities();
        DTOSubject[] secondPriorities = dtoStudent.getSecondPriorities();
        for (int i = 0; i < firstPriorities.length; i++)
        {
            if (i == 0)
            {
                student.setFirstPriorityA(Assembler.DTOSubjectToSubjectObject(firstPriorities[i]));
            } else
            {
                student.setFirstPriorityB(Assembler.DTOSubjectToSubjectObject(firstPriorities[i]));
            }
        }
        for (int i = 0; i < secondPriorities.length; i++)
        {
            if (i == 0)
            {
                student.setSecondPriorityA(Assembler.DTOSubjectToSubjectObject(secondPriorities[i]));
            } else
            {
                student.setSecondPriorityB(Assembler.DTOSubjectToSubjectObject(secondPriorities[i]));
            }
        }
        System.out.println(student);
        persist(student);
    }

    @Override
    public void setSubjects(ArrayList<DTOSubject> subjects)
    {
        for (DTOSubject dTOSubject : subjects)
        {
            Subject subject = Assembler.DTOSubjectToSubjectObject(dTOSubject);
            em.merge(subject);
        }
    }
}
