package com.hisen.service.impl;

import com.hisen.dao.AppointmentDao;
import com.hisen.dao.BookDao;
import com.hisen.dto.AppointExecution;
import com.hisen.entity.Appointment;
import com.hisen.entity.Book;
import com.hisen.enums.AppointStateEnum;
import com.hisen.exception.AppointException;
import com.hisen.exception.NoNumberException;
import com.hisen.exception.RepeatAppointException;
import com.hisen.service.BookService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hisenyuan on 2017/4/6 at 17:23.
 */
@Service
public class BookServiceImpl implements BookService {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  // 注入Service依赖
  @Autowired
  private BookDao bookDao;

  @Autowired
  private AppointmentDao appointmentDao;


  @Override
  public Book getById(long bookId) {
    return bookDao.queryById(bookId);
  }

  @Override
  public List<Book> getList(int start, int pageNum) {
    return bookDao.queryAll(start, pageNum);
  }

  @Override
  @Transactional
  /**
   * 使用注解控制事务方法的优点： 1.开发团队达成一致约定，明确标注事务方法的编程风格
   * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
   * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
   */
  public AppointExecution appoint(long bookId, long studentId) {
    try {
      // 减库存
      int update = bookDao.reduceNumber(bookId);
      if (update <= 0) {// 库存不足
        //return new AppointExecution(bookId, AppointStateEnum.NO_NUMBER);//错误写法
        throw new NoNumberException("no number");
      } else {
        // 执行预约操作
        int insert = appointmentDao.insertAppointment(bookId, studentId);
        if (insert <= 0) {// 重复预约
          //return new AppointExecution(bookId, AppointStateEnum.REPEAT_APPOINT);//错误写法
          throw new RepeatAppointException("repeat appoint");
        } else {// 预约成功
          Appointment appointment = appointmentDao.queryByKeyWithBook(bookId, studentId);
          return new AppointExecution(bookId, AppointStateEnum.SUCCESS, appointment);
        }
      }
      // 要先于catch Exception异常前先catch住再抛出，不然自定义的异常也会被转换为AppointException，导致控制层无法具体识别是哪个异常
    } catch (NoNumberException e1) {
      throw e1;
    } catch (RepeatAppointException e2) {
      throw e2;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      // 所有编译期异常转换为运行期异常
      //return new AppointExecution(bookId, AppointStateEnum.INNER_ERROR);//错误写法
      throw new AppointException("appoint inner error:" + e.getMessage());
    }
  }

  @Override
  public int addBook(Book book) {
    int i = bookDao.addBook(book);
    return i;
  }

  @Override
  public int countNum() {
    return bookDao.countNum();
  }

}
