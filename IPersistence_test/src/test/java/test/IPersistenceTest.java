package test;

import java.io.InputStream;
import java.util.List;

import com.github.dao.IUserDao;
import com.github.io.Resources;
import com.github.pojo.User;
import com.github.sqlsession.SqlSession;
import com.github.sqlsession.SqlSessionFactory;
import com.github.sqlsession.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * description
 * </p>
 *
 * @author isaac 2020/8/20 23:21
 * @since 1.0.0
 */
public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream in = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        user.setId(1L);
        user.setUsername("张三");
        // User result = sqlSession.selectOne("com.github.dao.IUserDao.findByCondition", user);
        // Assert.assertNotNull(result);
        // List<User> userList = sqlSession.selectList("com.github.dao.IUserDao.findAll");
        // Assert.assertNotNull(userList);

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        List<User> all = userDao.findAll();
        Assert.assertNotNull(all);
        User condition = userDao.findByCondition(user);
        Assert.assertNotNull(condition);

    }

}
