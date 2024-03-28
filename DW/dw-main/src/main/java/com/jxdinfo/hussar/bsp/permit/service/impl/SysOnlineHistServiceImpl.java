package com.jxdinfo.hussar.bsp.permit.service.impl;
/**
 * @ClassName: SysOnlineHistServiceImpl
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/12/24
 * @Version: 1.0
 */



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.vo.SysOrganVo;
import com.jxdinfo.hussar.bsp.permit.dao.SysOnlineHistMapper;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysOnline;
import com.jxdinfo.hussar.bsp.permit.model.SysOnlineHist;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.common.browserutil.GetBrowserUtil;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import java.sql.Timestamp;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Service;

@Service
public class SysOnlineHistServiceImpl extends ServiceImpl<SysOnlineHistMapper, SysOnlineHist> implements ISysOnlineHistService {
    @Resource
    private SysOnlineHistMapper sysOnlineHistMapper;
    @Resource
    private SysUsersMapper sysUsersMapper;
    @Resource
    private SysStruMapper sysStruMapper;
    @Resource
    private GetBrowserUtil getBrowserUtil;
    @Resource
    private ISysUsersService sysUsersService;

    public SysOnlineHistServiceImpl() {
    }

    public Page<SysOnlineHist> getOnlineHistList(Page<SysOnlineHist> page, Timestamp startTime, Timestamp endTime, String userAccount) {
        page.setRecords(this.sysOnlineHistMapper.getOnlineHistList(page, startTime, endTime, userAccount));
        return page;
    }

    public SysOnline addRecord() {
        String sessionId = (String)ShiroKit.getSession().getId();
        String userId = (String)ShiroKit.getSession().getAttribute("userId");
        if (ToolUtil.isEmpty(userId)) {
            return null;
        } else {
            SysUsers sysUsers = (SysUsers)this.sysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ACCOUNT", userId).ne("ACCOUNT_STATUS", UserStatus.DELETE.getCode()));
            if(sysUsers ==null){
                sysUsers =  (SysUsers)this.sysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ID", userId).ne("ACCOUNT_STATUS", UserStatus.DELETE.getCode()));
            }
            SysOrganVo dept = this.sysStruMapper.getOrgInfoByOrgId(sysUsers.getDepartmentId());
            String deptName = "";
            if (dept != null && dept.getShortName() != null) {
                deptName = dept.getShortName();
            }

            SysOnlineHist sysOnlineHist = (SysOnlineHist)this.sysOnlineHistMapper.selectById(sessionId);
            if (ToolUtil.isEmpty(sysOnlineHist)) {
                sysOnlineHist = new SysOnlineHist();
                sysOnlineHist.setSessionId(sessionId);
                sysOnlineHist.setUserId(sysUsers.getUserId());
                sysOnlineHist.setUserAccount(sysUsers.getUserAccount());
                sysOnlineHist.setUserName(sysUsers.getUserName());
                sysOnlineHist.setCorporationId(sysUsers.getDepartmentId());
                sysOnlineHist.setCorporationName(deptName);
                sysOnlineHist.setLoginTime(new Timestamp(System.currentTimeMillis()));
                sysOnlineHist.setRemoteaddr(HttpKit.getIp());
                sysOnlineHist.setRemotehost(HttpKit.getHost());
                sysOnlineHist.setRemoteport(HttpKit.getPort());
                HttpServletRequest requestNew = WebUtils.toHttp(HttpKit.getRequest());
                sysOnlineHist.setBrowserType(this.getBrowserUtil.getBrowser(requestNew));
                this.sysOnlineHistMapper.insert(sysOnlineHist);
            }

            SysOnline online = new SysOnline();
            online.setSessionId(sysOnlineHist.getSessionId());
            online.setUserId(sysOnlineHist.getUserId());
            online.setUserAccount(sysOnlineHist.getUserAccount());
            online.setUserName(sysOnlineHist.getUserName());
            online.setCorporationId(sysOnlineHist.getCorporationId());
            online.setCorporationName(sysOnlineHist.getCorporationName());
            online.setLoginTime(sysOnlineHist.getLoginTime());
            online.setRemoteaddr(sysOnlineHist.getRemoteaddr());
            online.setRemotehost(sysOnlineHist.getRemotehost());
            online.setRemoteport(sysOnlineHist.getRemoteport());
            online.setBrowserType(sysOnlineHist.getBrowserType());
            return online;
        }
    }

    public void updateLogoffTime() {
        String sessionId = (String)ShiroKit.getSession().getId();
        SysOnlineHist sysOnlineHist = (SysOnlineHist)this.sysOnlineHistMapper.selectById(sessionId);
        if (ToolUtil.isNotEmpty(sysOnlineHist)) {
            sysOnlineHist.setLogoffTime(new Timestamp(System.currentTimeMillis()));
            this.sysOnlineHistMapper.updateById(sysOnlineHist);
        }

    }
}
