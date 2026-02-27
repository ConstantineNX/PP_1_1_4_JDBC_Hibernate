package jm.task.core.jdbc;

import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.SessionFactory;


public class Main {
    public static void main(String[] args) {
        // реализуйте алгоритм здесь
        SessionFactory sessionFactory = Util.getSessionFactory();
        UserDaoHibernateImpl userDao = new UserDaoHibernateImpl(sessionFactory);

        userDao.saveUser("Petr", "Petrov", (byte) 23);
        userDao.saveUser("Sergey", "Sergeev", (byte) 33);
        userDao.saveUser("Victor", "Graf", (byte) 93);
        userDao.saveUser("Igor", "Ignorov", (byte) 23);

        for (User user : userDao.getAllUsers()) {
            System.out.println(user.getName() + " " + user.getLastName() + "-" + user.getAge());
        }

        userDao.cleanUsersTable();
        userDao.dropUsersTable();

        Util.closeSessionFactory();
    }
}
