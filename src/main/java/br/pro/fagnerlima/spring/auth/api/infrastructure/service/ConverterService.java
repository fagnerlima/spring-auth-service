package br.pro.fagnerlima.spring.auth.api.infrastructure.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ConverterService {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T convert(Object data, Class<T> destinationType) {
        return modelMapper.map(data, destinationType);
    }

    public <T> Page<T> convert(Page<?> dataPage, Class<T> destinationType) {
        return dataPage.map(data -> convert(data, destinationType));
    }

    public <T> List<T> convert(List<?> dataList, Class<T> destinationType) {
        return dataList.stream().map(data -> convert(data, destinationType)).collect(Collectors.toList());
    }

}
