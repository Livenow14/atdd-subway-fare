package wooteco.subway.entitymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {
    private static final Logger logger = LoggerFactory.getLogger(EntityManager.class);

    private final List<Object> origin;

    public EntityManager(Object... origin) {
        this.origin = List.of(origin);
    }

    public void dirtyChecking(Object... entity) {
        for (Object singleEntity : entity) {
            foo(singleEntity);
        }
    }

    private void foo(Object entity) {
        Map<String, Object> maps = new LinkedHashMap<>();

        Class<?> checkEntity = entity.getClass();
        Field[] declaredFields = checkEntity.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            try {
                declaredField.setAccessible(true);
                Object value = declaredField.get(entity);
                if (!value.toString().contains("domain")) {
                    maps.put(declaredField.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        maps.forEach((key, value) -> System.out.println("Key = " + key + "::" + "Value = " + value));
        String query = makeQuery(maps, checkEntity.getSimpleName());
        doQuery(maps, query);
    }

    private String makeQuery(Map<String, Object> maps, String name) {
        String query = "update " + name +
                " set " + makeColumnQuery(maps) +
                " where " + name +".id = ?";

        System.out.println("query = " + query);

        return query;
    }

    private String makeColumnQuery(Map<String, Object> maps) {
        StringBuilder query = new StringBuilder();

        for (String key : maps.keySet()) {
            query.append(camelToSnake(key)).append(" = ?").append(", ");
        }
        int length = query.toString().length();
        return query.deleteCharAt(length-2).toString();
    }

    private String camelToSnake(String str) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";

        str = str
                .replaceAll(
                        regex, replacement)
                .toLowerCase();
        return str;
    }

    private void doQuery(Map<String, Object> maps, String query) {
        Connection con = ConnectionUtil.getConnection();
        try {
            PreparedStatement preparedStatement = con.prepareStatement(query);
            for (int i = 1; i <= maps.size(); i++) {
                // TODO: 인자를 어떻게 넣지 ?
                //preparedStatement.
            }
            preparedStatement.executeUpdate();
            closeResources(con, preparedStatement);
        } catch (SQLException e) {
            logger.error("db 오류" + e.getMessage());
            e.printStackTrace();
        }

    }

    private void closeResources(Connection connection, PreparedStatement preparedStatement) throws SQLException {
        ConnectionUtil.closeConnection(connection);
        preparedStatement.close();
    }
}