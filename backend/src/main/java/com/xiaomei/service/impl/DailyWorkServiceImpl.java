package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.DailyWork;
import com.xiaomei.entity.OrderTrack;
import com.xiaomei.entity.ProductSchedule;
import com.xiaomei.mapper.DailyWorkMapper;
import com.xiaomei.mapper.OrderTrackMapper;
import com.xiaomei.mapper.ProductScheduleMapper;
import com.xiaomei.service.DailyWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DailyWorkServiceImpl implements DailyWorkService {

    @Autowired
    private DailyWorkMapper workMapper;
    @Autowired
    private ProductScheduleMapper scheduleMapper;
    @Autowired
    private OrderTrackMapper orderTrackMapper;

    @Override
    public PageObject<DailyWork> queryList(BaseQuery query) {
        Integer factoryId = query.getFactoryId();
        if (factoryId == null) return PageObject.error("工厂ID不能为空");
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<DailyWork> list = workMapper.selectList(factoryId, offset, query.getPageSize());
        Long total = workMapper.selectCount(factoryId);
        return new PageObject<>(list, query.getPageNum(), query.getPageSize(), total);
    }

    @Override
    public DailyWork queryById(Integer id) { return workMapper.selectById(id); }

    @Override
    public Map<String, String> report(DailyWork work) {
        if (work.getScheduleId() == null) return error("调度ID不能为空");
        if (work.getFactoryId() == null) return error("工厂ID不能为空");
        if (work.getWorkingCount() == null || work.getWorkingCount() <= 0) return error("加工数量必须大于0");

        ProductSchedule schedule = scheduleMapper.selectById(work.getScheduleId());
        if (schedule == null) return error("工单不存在");
        if (schedule.getScheduleStatus() != StatusEnum.SCHEDULE_PRODUCING) return error("只能对生产中的工单报工");

        work.setCompleteFlag(StatusEnum.WORK_COMPLETE_NO);
        if (work.getStartTime() == null) work.setStartTime(new Date());
        work.setEquipmentId(schedule.getEquipmentId());
        work.setEquipmentSeq(schedule.getScheduleSeq());
        workMapper.insert(work);

        // 更新订单跟踪表
        OrderTrack track = new OrderTrack();
        track.setScheduleId(work.getScheduleId());
        track.setPlanId(schedule.getPlanId());
        track.setProductId(schedule.getProductId());
        track.setWorkingCount(work.getWorkingCount());
        track.setQualifiedCount(work.getQualifiedCount() != null ? work.getQualifiedCount() : 0);
        track.setFactoryId(work.getFactoryId());
        track.setCreateUserid(work.getCreateUserid());
        track.setUpdateUserid(work.getUpdateUserid());
        orderTrackMapper.insert(track);

        return success("报工成功");
    }

    @Override
    public Map<String, String> completeReport(Integer id, Integer updateUserid) {
        DailyWork work = workMapper.selectById(id);
        if (work == null) return error("报工记录不存在");
        if (work.getCompleteFlag() == StatusEnum.WORK_COMPLETE_YES) return error("已结束报工");
        work.setCompleteFlag(StatusEnum.WORK_COMPLETE_YES);
        work.setEndTime(new Date());
        work.setUpdateUserid(updateUserid);
        workMapper.update(work);
        return success("结束报工");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        workMapper.delete(id, updateUserid);
        return success("删除成功");
    }

    private Map<String, String> success(String msg) { return Map.of("status", "ok", "msg", msg); }
    private Map<String, String> error(String msg) { return Map.of("status", "error", "msg", msg); }
}
