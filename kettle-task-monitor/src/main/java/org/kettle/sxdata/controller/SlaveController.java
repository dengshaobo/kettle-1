package org.kettle.sxdata.controller;

import net.sf.json.JSONObject;
import org.kettle.ext.utils.JsonArray;
import org.kettle.sxdata.bean.PageforBean;
import org.kettle.sxdata.entity.SlaveEntity;
import org.kettle.sxdata.entity.UserGroupAttributeEntity;
import org.kettle.sxdata.service.SlaveService;
import org.kettle.sxdata.util.task.KettleEncr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;


/**
 * @description: 从属控制器
 * @author: ZX
 * @date: 2018/11/20 11:48
 */
@Controller
@RequestMapping(value = "/slave")
public class SlaveController {

    @Autowired
    protected SlaveService slaveService;

    /**
     * 修改节点服务信息前 先获得需要修改的节点的信息
     *
     * @param response
     * @param request
     * @param slaveId
     * @throws Exception
     */
    @RequestMapping(value = "/getSlaveServerInfo")
    @ResponseBody
    protected void getSlaveServerInfo(HttpServletResponse response, HttpServletRequest request, @RequestParam String slaveId) throws Exception {
        try {
            SlaveEntity slave = slaveService.getSlaveByHostName(Integer.valueOf(slaveId));
            JSONObject json = new JSONObject();
            json.put("name", slave.getName());
            json.put("hostname", slave.getHostName());
            json.put("port", slave.getPort());
            json.put("webAppName", slave.getWebappName());
            json.put("username", slave.getUsername());
            json.put("password", KettleEncr.decryptPasswd(slave.getPassword()));
            json.put("master", slave.getMaster());
            json.put("slaveId", slave.getSlaveId());
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(json.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 修改节点服务
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/updateSlaveServer")
    @ResponseBody
    protected void updateSlaveServer(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            String result = slaveService.updateSlave(request);
            PrintWriter out = response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获得节点信息 以panel形式显示
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/getSlave")
    @ResponseBody
    protected void getJobs(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
            String userGroupName = "";
            if (null != attr) {
                userGroupName = attr.getUserGroupName();
            }
            List<SlaveEntity> result = slaveService.getAllSlave(userGroupName);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(JsonArray.fromObject(result).toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取节点信息 以下拉列表形式展现
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/getSlaveSelect")
    @ResponseBody
    protected void getSlaveSelect(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            StringBuffer sbf = new StringBuffer("[");
            UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
            String userGroupName = "";
            if (null != attr) {
                userGroupName = attr.getUserGroupName();
            }
            List<SlaveEntity> slaves = slaveService.getAllSlave(userGroupName);
            for (int i = 0; i < slaves.size(); i++) {
                String thisSlaveJson = "";
                String host = "\"" + slaves.get(i).getHostName() + "\"";
                String hostId = "\"" + "hostId" + "\"";
                String hostName = "\"" + "hostName" + "\"";
                if (i != slaves.size() - 1) {
                    thisSlaveJson = "{" + hostId + ":" + host + "," + hostName + ":" + host + "},";
                } else {
                    thisSlaveJson = "{" + hostId + ":" + host + "," + hostName + ":" + host + "}";
                }
                sbf.append(thisSlaveJson);
            }
            sbf.append("]");
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(sbf.toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 所有节点的指标信息
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/allSlaveQuato")
    @ResponseBody
    protected void allSlaveQuato(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            //获取当前用户所在的用户组
            UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
            String userGroupName = "";
            if (null != attr) {
                userGroupName = attr.getUserGroupName();
            }
            String result = slaveService.allSlaveQuato(userGroupName);
            PrintWriter out = response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }


    /**
     * 节点管理列表
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/slaveManager")
    @ResponseBody
    protected void slaveManager(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            Integer start = Integer.valueOf(request.getParameter("start"));
            Integer limit = Integer.valueOf(request.getParameter("limit"));
            UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
            String userGroupName = "";
            if (null != attr) {
                userGroupName = attr.getUserGroupName();
            }
            PageforBean result = slaveService.findSlaveByPageInfo(start, limit, userGroupName);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(JSONObject.fromObject(result).toString());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 删除节点
     *
     * @param response
     * @param request
     * @param slaveId
     * @throws Exception
     */
    @RequestMapping(value = "/deleteSlave")
    @ResponseBody
    protected void deleteSlave(HttpServletResponse response, HttpServletRequest request, @RequestParam String slaveId) throws Exception {
        try {
            slaveService.deleteSlave(Integer.valueOf(slaveId));
            PrintWriter out = response.getWriter();
            out.write("{success:true}");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 节点体检
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/slaveTest")
    @ResponseBody
    protected void slaveTest(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            String hostName = request.getParameter("hostName");
            String result = slaveService.slaveTest(hostName);
            PrintWriter out = response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 单独对节点的某项指标进行监
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/slaveQuatoByCondition")
    @ResponseBody
    protected void slaveQuatoByCondition(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            String chooseDate = request.getParameter("chooseDate");
            String quatoType = request.getParameter("quatoTypeValue");
            String maxOrAvg = request.getParameter("maxOrAvg");
            //默认使用折线图作为展现方式
            UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
            String userGroupName = "";
            if (null != attr) {
                userGroupName = attr.getUserGroupName();
            }
            String result = slaveService.slaveQuatoByCondition(quatoType, "折线图", maxOrAvg, chooseDate, userGroupName);

            if (null == result) {
                result = "";
            }
            PrintWriter out = response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 新增节点
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/addSlave")
    @ResponseBody
    protected void addSlave(HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            String result = slaveService.addSlave(request);
            PrintWriter out = response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}