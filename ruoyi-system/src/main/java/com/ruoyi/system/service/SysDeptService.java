package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.req.SysDeptCreateReq;
import com.ruoyi.system.domain.dto.req.SysDeptQueryReq;
import com.ruoyi.system.domain.dto.req.SysDeptUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDeptResp;
import java.util.List;

/**
 * 部门业务服务接口
 */
public interface SysDeptService {

    List<SysDeptResp> getDeptList(SysDeptQueryReq queryReq);

    SysDeptResp getDeptById(Long deptId);

    int createDept(SysDeptCreateReq req);

    int updateDept(SysDeptUpdateReq req);

    int deleteDeptById(Long deptId);
}
