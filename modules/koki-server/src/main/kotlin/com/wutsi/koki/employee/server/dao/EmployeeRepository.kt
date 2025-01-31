package com.wutsi.koki.employee.server.dao

import com.wutsi.koki.employee.server.domain.EmployeeEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : CrudRepository<EmployeeEntity, Long>
