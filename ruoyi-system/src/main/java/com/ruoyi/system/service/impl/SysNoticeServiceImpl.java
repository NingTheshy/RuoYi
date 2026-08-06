package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysNoticeConvert;
import com.ruoyi.system.domain.dto.req.SysNoticeCreateReq;
import com.ruoyi.system.domain.dto.req.SysNoticeQueryReq;
import com.ruoyi.system.domain.dto.req.SysNoticeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysNoticeResp;
import com.ruoyi.system.domain.entity.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.SysNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    private final SysNoticeConvert noticeConvert;

    public SysNoticeServiceImpl(SysNoticeConvert noticeConvert) {
        this.noticeConvert = noticeConvert;
    }

    @Override
    public PageResult<SysNoticeResp> getNoticePage(SysNoticeQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysNotice query = noticeConvert.toEntity(queryReq);
        Page<SysNotice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getNoticeTitle()), SysNotice::getNoticeTitle, query.getNoticeTitle())
                .eq(StringUtils.hasText(query.getNoticeType()), SysNotice::getNoticeType, query.getNoticeType())
                .eq(StringUtils.hasText(query.getStatus()), SysNotice::getStatus, query.getStatus())
                .like(StringUtils.hasText(query.getCreateBy()), SysNotice::getCreateBy, query.getCreateBy())
                .orderByDesc(SysNotice::getNoticeId);
        Page<SysNotice> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(noticeConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public SysNoticeResp getNoticeById(Long noticeId) {
        SysNotice notice = getById(noticeId);
        if (notice == null) {
            throw new ServiceException(404, "公告不存在");
        }
        return noticeConvert.toResp(notice);
    }

    @Override
    @Transactional
    public int createNotice(SysNoticeCreateReq req) {
        SysNotice notice = noticeConvert.toEntity(req);
        if (notice.getStatus() == null) {
            notice.setStatus(StatusFlag.NORMAL.getCode());
        }
        if (notice.getNoticeType() == null) {
            notice.setNoticeType("1");
        }
        return save(notice) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateNotice(SysNoticeUpdateReq req) {
        SysNotice existing = getById(req.getNoticeId());
        if (existing == null) {
            throw new ServiceException(404, "公告不存在");
        }
        SysNotice notice = noticeConvert.toEntity(req);
        return updateById(notice) ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteNoticeByIds(Long[] noticeIds) {
        return removeByIds(Arrays.asList(noticeIds)) ? noticeIds.length : 0;
    }
}
