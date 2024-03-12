package com.learning.awspring.web.mapper;

import com.learning.awspring.domain.CustomerDTO;
import com.learning.awspring.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);
}
