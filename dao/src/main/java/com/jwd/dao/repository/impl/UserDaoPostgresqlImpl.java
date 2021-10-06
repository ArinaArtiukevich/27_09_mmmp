package com.jwd.dao.repository.impl;

import com.jwd.dao.config.DataBaseConfig;
import com.jwd.dao.domain.User;
import com.jwd.dao.domain.UserDto;
import com.jwd.dao.repository.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDaoPostgresqlImpl implements UserDao {

    private static final String FIND_ALL_USERS_QUERY = "SELECT u.id, u.login, u.firstname, u.lastname FROM users u;";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT u.id, u.login, u.firstname, u.lastname FROM users u WHERE id = ?;";
    private static final String FIND_USER_BY_LOGIN_AND_PASSWORD_QUERY = "SELECT u.id, u.login, u.firstname, u.lastname FROM users u WHERE login = ? AND password = ?;";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (login, firstname, lastname, password) VALUES(?, ?, ?, ?);";
    private final DataBaseConfig dataBaseConfig;

    public UserDaoPostgresqlImpl() {
        dataBaseConfig = new DataBaseConfig();
    }

    @Override
    public List<UserDto> getUsers() {
        // with finally :
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        ResultSet resultSet = null;
        try (
                Connection connection = dataBaseConfig.getConnection();
                PreparedStatement preparedStatement = getPreparedStatement(FIND_ALL_USERS_QUERY, connection, Collections.emptyList());
               // or PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERS_QUERY);
                ResultSet resultSet = preparedStatement.executeQuery()// представление реляционной таблицы
        ) {
            // try с ресурсами тк все AutoCloseable

            final List<UserDto> users = new ArrayList<>();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String login = resultSet.getString(2);
                String fn = resultSet.getString(3);
                String ln = resultSet.getString(4);
                users.add(new UserDto(id, login, fn, ln));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        // если не try с ресурсами
//        finally {
//            dataBaseConfig.close(connection, preparedStatement, resultSet);
//        }
    }

    @Override
    public UserDto getUserById(Long id) {
        List<Object> parameters = Arrays.asList(
                id
        );
        try (
                Connection connection = dataBaseConfig.getConnection();
               // PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_QUERY);
                PreparedStatement preparedStatement = getPreparedStatement(FIND_USER_BY_ID_QUERY, connection, parameters);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            UserDto userDto = null;
            while (resultSet.next()) {
                long foundId = resultSet.getLong(1);
                String login = resultSet.getString(2);
                String fn = resultSet.getString(3);
                String ln = resultSet.getString(4);
                userDto = new UserDto(foundId, login, fn, ln);
            }
            return userDto;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public UserDto getUserByLoginAndPassword(User user) {
        List<Object> parameters = Arrays.asList(
                user.getLogin(),
                user.getPassword()
        );
        try (
                Connection connection = dataBaseConfig.getConnection();
                PreparedStatement preparedStatement = getPreparedStatement(FIND_USER_BY_LOGIN_AND_PASSWORD_QUERY, connection, parameters);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            UserDto userDto = null;
            while (resultSet.next()) {
                long foundId = resultSet.getLong(1);
                String login = resultSet.getString(2);
                String fn = resultSet.getString(3);
                String ln = resultSet.getString(4);
                userDto = new UserDto(foundId, login, fn, ln);
            }
            return userDto;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public UserDto saveUser(User user) {
        List<Object> parameters = initSaveUserParameters(user);
        try (
                Connection connection = dataBaseConfig.getConnection();
                PreparedStatement preparedStatement = getPreparedStatement(INSERT_USER_QUERY, connection, parameters);
        ) {
            int affectedRows = preparedStatement.executeUpdate(); // вернет сколько строк записано
            UserDto userDto = null;
            if (affectedRows > 0) {
                userDto = new UserDto(user);
            }
            return userDto;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private List<Object> initSaveUserParameters(User user) {
        return Arrays.asList(
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword()
        );
    }

    public static void main(String[] args) {

        UserDaoPostgresqlImpl dao = new UserDaoPostgresqlImpl();
        System.out.println("UserDto getUserById(Long id) :");
        System.out.println(dao.getUserById(2L));

        System.out.println();
        System.out.println("UserDto getUserByLoginAndPassword(User user) :");
        System.out.println(dao.getUserByLoginAndPassword(new User(null, "serg", null, null, "2")));

        System.out.println();
        System.out.println("UserDto saveUser(User user) :");
        System.out.println(dao.saveUser(new User(null, "c", "", "", "a")));

        System.out.println();
        System.out.println("List<UserDto> getUsers() :");
        dao.getUsers().forEach(System.out::println);


//        UUID uniqueKey = UUID.randomUUID(); // генератор ID
//        System.out.println(uniqueKey);
//        String uniqueID = UUID.randomUUID().toString();
//        System.out.println(uniqueID);
    }

    private PreparedStatement getPreparedStatement(String query, Connection connection, List<Object> parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        setPreparedStatementParameters(preparedStatement, parameters);
        return preparedStatement;
    }

    private void setPreparedStatementParameters(PreparedStatement preparedStatement, List<Object> parameters) throws SQLException {
        for (int i = 0, queryParameterIndex = 1; i < parameters.size(); i++, queryParameterIndex++) {
            Object parameter = parameters.get(i);
            setPreparedStatementParameter(preparedStatement, queryParameterIndex, parameter);

        }
    }

    private void setPreparedStatementParameter(PreparedStatement preparedStatement, int queryParameterIndex, Object parameter) throws SQLException {
        if (Long.class == parameter.getClass()) {
            preparedStatement.setLong(queryParameterIndex, (Long) parameter);
        } else if (String.class == parameter.getClass()) {
            preparedStatement.setString(queryParameterIndex, (String) parameter);
        }
    }
}




//
//SELECT * FROM users;
//
//        -- INSERT INTO users (id, login, firstname, lastname, password)
//        -- VALUES(3,'pavel', 'Pavel', 'Pavlov', '3');
//
//        --  DELETE FROM users WHERE id = 2;
//
//        -- UPDATE users SET firstname = NULL, lastname = NULL WHERE id = 1;
//
//        -- SELECT u.id, u.login, u.firstname, u.lastname FROM users u;
//        -- SELECT u.id, u.login, u.firstname, u.lastname FROM users u WHERE id = 2;
//
//        -- ALTER TABLE users DROP CONSTRAINT users_pkey;
//        -- ALTER TABLE users DROP COLUMN id;
//
//        -- ALTER TABLE users ADD COLUMN id BIGSERIAL PRIMARY KEY;