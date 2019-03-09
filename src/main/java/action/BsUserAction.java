package action;

import common.BsFactory;
import common.BsPageList;
import dao.BsUserDao;
import exception.MyException;
import domain.BsUser;
import iservice.IBsUserService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制类
 */

public class BsUserAction extends BsBaseAction {

    private static final long serialVersionUID = 1L;
    private final static int PAGE_SIZE = 10;
    private int ADMIN_RIGHT = 2;  // 管理员权限
    private IBsUserService userService = (IBsUserService) BsFactory.getBean("userService");

    private BsUser user;
    private Integer userId;
    private BsPageList<BsUser> pageList;  // 分页器
    private Integer pageNo;

    // 添加新用户
    @Override
    protected void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            userService.addUser(getUserInfo(request));
            response.sendRedirect("/index.jsp");  // 返回主页
        } catch (Exception e) {
            request.setAttribute("msg", e.getMessage() + "<a href=\"JavaScript:window.history.back()\">返回</a>");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/common/message.jsp");  // 跳转到信息页
            requestDispatcher.forward(request, response);
        }
    }

    // 修改用户信息
    @Override
    protected void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            userService.editUser(getUserInfo(request));
            response.sendRedirect("/index.jsp");  // 返回主页
        } catch (Exception e) {
            request.setAttribute("msg", e.getMessage() + "<a href=\"JavaScript:window.history.back()\">返回</a>");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/common/message.jsp");  // 跳转到信息页
            requestDispatcher.forward(request, response);
        }
    }

    // 查看所有用户信息
    @Override
    protected void manage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (pageNo == null) {
            pageNo = 1;
        }

        try {
            List<BsUser> list = userService.findUsers(pageNo, PAGE_SIZE);  // 分页查找用户数据
            int count = userService.findCount();
            pageList = new BsPageList<BsUser>(list, count, pageNo, PAGE_SIZE, "/bs/BsUserAction?method=manage");
            for (BsUser user : pageList.getList()) {
                System.out.println(user.toString());
            }
            request.setAttribute("pageList", pageList);

            response.sendRedirect("/bs/user/manage.jsp");
        } catch (Exception e) {
            request.setAttribute("msg", e.getMessage() + "<a href=\"JavaScript:window.history.back()\">返回</a>");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/common/message.jsp");  // 跳转到信息页
            requestDispatcher.forward(request, response);
        }

    }

    // 查看用户
    @Override
    protected void show(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            BsUser user = userService.findUserById(Integer.parseInt(request.getParameter("userId")));
            request.setAttribute("user", user);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/bs/user/show.jsp");
            requestDispatcher.forward(request, response);
        } catch (Exception e) {
            request.setAttribute("msg", e.getMessage() + "<a href=\"JavaScript:window.history.back()\">返回</a>");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/common/message.jsp");  // 跳转到信息页
            requestDispatcher.forward(request, response);
        }
    }

    // 删除用户
    @Override
    protected void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            userService.deleteUser(Integer.parseInt(request.getParameter("userId")));
            response.sendRedirect("/User/BsUserAction?method=manage");
        } catch (MyException e) {
            request.setAttribute("msg", e.getMessage() + "<a href=\"JavaScript:window.history.back()\">返回</a>");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/common/message.jsp");
            requestDispatcher.forward(request, response);
        }
    }

    // 登录用户
    protected void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        BsUser userInfo = getUserInfo(request);

        try {
            BsUser user = userService.findUserByName(userInfo.getUserName(), userInfo.getUserPwd());  // 查找用户
            HttpSession session = request.getSession();

            if (user != null) {
                session.setAttribute("user", user);

                if (user.getUserRight() >= ADMIN_RIGHT) {  // 管理员权限
                    response.sendRedirect("/bs/admin/index.jsp");  // 跳转到管理员主页
                } else {
                    response.sendRedirect("index.jsp");
                }
            }
        } catch (Exception e) {
            request.setAttribute("msg", e.getMessage() + "<a href=\"JavaScript:window.history.back()\">返回</a>");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/bs/common/message.jsp");  // 跳转到信息页
            requestDispatcher.forward(request, response);
        }

    }

    // 获取用户信息
    private BsUser getUserInfo(HttpServletRequest request) {

        BsUser user = new BsUser();
        user.setUserName(request.getParameter("userName"));
        user.setUserPwd(request.getParameter("password"));
        user.setUserRealName(request.getParameter("realName"));
        user.setUserPhone(request.getParameter("phone"));
        user.setUserEmail(request.getParameter("email"));
        user.setUserAddr(request.getParameter("address"));

        return user;
    }


}
