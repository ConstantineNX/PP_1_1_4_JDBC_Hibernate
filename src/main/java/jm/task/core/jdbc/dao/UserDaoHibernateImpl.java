package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UserDaoHibernateImpl implements UserDao {
    private final SessionFactory sessionFactory;

    public UserDaoHibernateImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private void executeInTransaction(Consumer<Session> action) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
        } catch (HibernateException e) {
            System.out.println("Ошибка в Hibernate: " + e.getMessage());
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (HibernateException e1) {
                    System.out.println("Ошибка отката транзакции: " + e1.getMessage());
                }
            }
        }
    }

    @Override
    public void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "`name` VARCHAR(255), " +
                "lastName VARCHAR(255), " +
                "age TINYINT)";
        executeInTransaction(session -> {
            session.createNativeQuery(sql).executeUpdate();
        });
    }

    @Override
    public void dropUsersTable() {
        String sql = "DROP TABLE IF EXISTS users";
        executeInTransaction(session -> {
            session.createNativeQuery(sql).executeUpdate();
        });
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        executeInTransaction(session -> {
            User user = new User(name, lastName, age);
            session.save(user);
            System.out.println("User с именем - " + name + " добавлен в базу данных");
        });
    }

    @Override
    public void removeUserById(long id) {
        executeInTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
        });
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (HibernateException e) {
            System.out.println("Ошибка при получении списка пользователей: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception ex) {
            System.out.println("Другая ошибка при получении списка пользователей: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void cleanUsersTable() {
        String sql = "DELETE FROM users";
        executeInTransaction(session -> {
            session.createNativeQuery(sql).executeUpdate();
        });
    }
}
