package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.CustomerConvert;
import com.crm.entity.Customer;
import com.crm.entity.Department;
import com.crm.entity.Manager;
import com.crm.entity.SysManager;
import com.crm.mapper.CustomerMapper;
import com.crm.mapper.CustomerMapper;

import com.crm.mapper.DepartmentMapper;
import com.crm.mapper.ManagerMapper;
import com.crm.query.CustomerQuery;
import com.crm.query.IdQuery;
import com.crm.security.user.ManagerDetail;
import com.crm.security.user.SecurityUser;
import com.crm.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.ExcelUtils;
import com.crm.vo.CustomerVO;
import com.fhs.common.utils.StringUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
@AllArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    private DepartmentMapper departmentMapper;
    private ManagerMapper managerMapper;


    @Override
    public PageResult<CustomerVO> getPage(CustomerQuery query) {
        Page<CustomerVO> page = new Page<>(query.getPage(), query.getLimit());
        MPJLambdaWrapper<Customer> wrapper = buildPermissionWrapper(query);

        Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Override
    public void exportCustomer(CustomerQuery query, HttpServletResponse httpResponse) {
        MPJLambdaWrapper<Customer> wrapper = selection(query);
        List<Customer> customerList = baseMapper.selectJoinList(wrapper);
        ExcelUtils.writeExcel(httpResponse, customerList, "客户信息",  "客户信息", CustomerVO.class);
    }

    @Override
    public void saveOrUpdate(CustomerVO customerVO) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().eq(Customer::getPhone, customerVO.getPhone());
        if (customerVO.getId() == null) {
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null) {
                throw new ServerException("该手机号客户已存在，请勿重复添加");
            }
            Customer convert = CustomerConvert.INSTANCE.convert(customerVO);
            Integer managerId = SecurityUser.getManagerId();
            convert.setCreaterId(managerId);
            convert.setOwnerId(managerId);
            baseMapper.insert(convert);
        } else {
            wrapper.ne(Customer::getId, customerVO.getId());
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null) {
                throw new ServerException("该手机号客户已存在，请勿重复添加");
            }
            Customer convert = CustomerConvert.INSTANCE.convert(customerVO);
            baseMapper.updateById(convert);
        }
    }

    @Override
    public void removeCustomer(List<Integer> ids) {
        removeByIds(ids);
    }

    @Override
    public void customerToPublicPool(IdQuery idQuery) {
        Customer customer = baseMapper.selectById(idQuery.getId());
        if(customer == null){
            throw new ServerException("客户不存在,无法转入公海");
        }
        customer.setIsPublic(1);
        customer.setOwnerId(null);
        baseMapper.updateById(customer);
    }

    @Override
    public void publicPoolToPrivate(IdQuery idQuery) {
        Customer customer = baseMapper.selectById(idQuery.getId());
        if (customer == null) {
            throw new ServerException("客户不存在,无法转入公海");
        }
        customer.setIsPublic(0);
        Integer ownerId = SecurityUser.getManagerId();
        customer.setOwnerId(ownerId);
        baseMapper.updateById(customer);
    }

    private MPJLambdaWrapper<Customer> selection(CustomerQuery query) {
        MPJLambdaWrapper<Customer> wrapper = new MPJLambdaWrapper<>();

        wrapper.selectAll(Customer.class)
                .selectAs("o", SysManager::getAccount,CustomerVO::getOwnerName)
                .selectAs("c", SysManager::getAccount,CustomerVO::getName)
                .leftJoin(SysManager.class,"o",SysManager::getId,Customer::getOwnerId)
                .leftJoin(SysManager.class,"c",SysManager::getId,Customer::getCreaterId);



        if(StringUtils.isNotBlank(query.getName())){
            wrapper.like(Customer::getName,query.getName());
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            wrapper.like(Customer::getPhone, query.getPhone());
        }

        if (query.getLevel() != null) {
            wrapper.eq(Customer::getLevel, query.getLevel());
        }

        if (query.getSource() != null) {
            wrapper.eq(Customer::getSource, query.getSource());
        }

        if (query.getFollowStatus() != null) {
            wrapper.eq(Customer::getFollowStatus, query.getFollowStatus());
        }

        if (query.getIsPublic() != null) {
            wrapper.eq(Customer::getIsPublic, query.getIsPublic());
        }
// 4、构建列表排序
        wrapper.orderByDesc(Customer::getCreateTime);
        return wrapper;
    }
    // 提取权限处理公共方法
    private MPJLambdaWrapper<Customer> buildPermissionWrapper(CustomerQuery query) {
        MPJLambdaWrapper<Customer> wrapper = selection(query);
        ManagerDetail currentUser = SecurityUser.getManager();

        if (currentUser == null || currentUser.getId() == null) {
            wrapper.eq("1", "0"); // 返回无数据的条件
            return wrapper;
        }

        Integer currentUserId = currentUser.getId();
        String currentUsername = currentUser.getUsername();
        Integer currentDepartId = currentUser.getDepartId();

        if ("admin".equals(currentUsername)) {
            return wrapper; // 管理员返回所有条件
        }

        if (query.getIsPublic() != null && query.getIsPublic() == 1) {
            wrapper.eq(Customer::getIsPublic, 1);
            return wrapper;
        }

        wrapper.eq(Customer::getIsPublic, 0);

        if (currentDepartId == null) {
            wrapper.eq(Customer::getOwnerId, currentUserId);
            return wrapper;
        }

        LambdaQueryWrapper<Department> departWrapper = new LambdaQueryWrapper<>();
        departWrapper.eq(Department::getId, currentDepartId)
                .or().eq(Department::getParentId, currentDepartId);
        List<Department> departments = departmentMapper.selectList(departWrapper);

        if (departments.isEmpty()) {
            wrapper.eq(Customer::getOwnerId, currentUserId);
            return wrapper;
        }

        List<Integer> departIds = departments.stream()
                .map(Department::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Manager> managerWrapper = new LambdaQueryWrapper<>();
        managerWrapper.in(Manager::getDepartId, departIds);
        List<Manager> managers = managerMapper.selectList(managerWrapper);

        List<Integer> ownerIds = managers.stream()
                .map(Manager::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!ownerIds.contains(currentUserId)) {
            ownerIds.add(currentUserId);
        }

        wrapper.in(Customer::getOwnerId, ownerIds);
        return wrapper;
    }
}
//    @Override
//    public PageResult<CustomerVO> getPage(CustomerQuery query) {
//        Page<CustomerVO> page = new Page<>(query.getPage(), query.getLimit());
//        MPJLambdaWrapper<Customer> wrapper = selection(query);
//        Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
//        return new PageResult<>(result.getRecords(), result.getTotal());
//    }

//    @Override
//    public PageResult<CustomerVO> getPage(CustomerQuery query) {
//        Page<CustomerVO> page = new Page<>(query.getPage(), query.getLimit());
//        MPJLambdaWrapper<Customer> wrapper = selection(query);
//        ManagerDetail currentUser = SecurityUser.getManager();
//        if (currentUser == null || currentUser.getId() == null) {
//            return new PageResult<>(Collections.emptyList(), 0L);
//        }
//        Integer currentUserId = currentUser.getId();
//        String currentUsername = currentUser.getUsername();
//        Integer currentDepartId = currentUser.getDepartId();
//
//        // 管理员权限：查看所有客户
//        if ("admin".equals(currentUsername)) {
//            Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
//            return new PageResult<>(result.getRecords(), result.getTotal());
//        }
//
//        // 公海客户查询：所有用户可见
//        if (query.getIsPublic() != null && query.getIsPublic() == 1) {
//            wrapper.eq(Customer::getIsPublic, 1);
//            Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
//            return new PageResult<>(result.getRecords(), result.getTotal());
//        }
//        // 个人客户查询：当前部门+子部门用户的客户
//        wrapper.eq(Customer::getIsPublic, 0); // 仅个人客户
//        // 无部门用户：仅看自己的客户
//        if (currentDepartId == null) {
//            wrapper.eq(Customer::getOwnerId, currentUserId);
//            // 1. 先执行分页查询，获取Page对象
//            Page<CustomerVO> queryResult = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
//            // 2. 提取records和total，传给PageResult
//            return new PageResult<>(queryResult.getRecords(), queryResult.getTotal());
//        }
//        LambdaQueryWrapper<Department> departWrapper = new LambdaQueryWrapper<>();
//        departWrapper.eq(Department::getId, currentDepartId) // 当前部门
//                .or().eq(Department::getParentId, currentDepartId); // 直接子部门
//        List<Department> departments = departmentMapper.selectList(departWrapper);
//        if (departments.isEmpty()) {
//            wrapper.eq(Customer::getOwnerId, currentUserId);
//            // 同样修复此处的PageResult构造参数
//            Page<CustomerVO> queryResult = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
//            return new PageResult<>(queryResult.getRecords(), queryResult.getTotal());
//        }
//        // 提取部门ID，查询下属所有用户
//        List<Integer> departIds = departments.stream()
//                .map(Department::getId)
//                .collect(Collectors.toList());
//
//        LambdaQueryWrapper<Manager> managerWrapper = new LambdaQueryWrapper<>();
//        managerWrapper.in(Manager::getDepartId, departIds);
//        List<Manager> managers = managerMapper.selectList(managerWrapper);
//        // 提取用户ID（含当前用户）
//        List<Integer> ownerIds = managers.stream()
//                .map(Manager::getId)
//                .filter(id -> id != null)
//                .collect(Collectors.toList());
//        if (!ownerIds.contains(currentUserId)) {
//            ownerIds.add(currentUserId);
//        }
//
//        wrapper.in(Customer::getOwnerId, ownerIds);
//
//        Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
//        return new PageResult<>(result.getRecords(), result.getTotal());
//    }