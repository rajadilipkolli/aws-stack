package com.learning.awspring.web.mapper;

import com.learning.awspring.domain.CustomerDTO;
import com.learning.awspring.entities.Customer;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

  Customer toEntity(CustomerDTO customerDTO);
}
