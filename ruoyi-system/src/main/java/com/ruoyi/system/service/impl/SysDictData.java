package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysDictDataConvert;
import com.ruoyi.system.domain.dto.req.SysDictDataCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictDataQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictDataUpdateReq;
import com.ruoyi.system.domain.dto.resp.DictDataOptionResp;
import com.ruoyi.system.domain.dto.resp.SysDictDataResp;
import com.ruoyi.system.domain.entity.SysDictData;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.SysDictDataService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

