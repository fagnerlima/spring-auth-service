package br.pro.fagnerlima.spring.auth.api.infrastructure.facade;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.InformationNotFoundException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.annotation.converter.IdReference;
import br.pro.fagnerlima.spring.auth.api.infrastructure.factory.RepositoryFactory;
import br.pro.fagnerlima.spring.auth.api.infrastructure.service.exception.IdReferenceException;
import br.pro.fagnerlima.spring.auth.api.infrastructure.util.FieldUtils;

@Component
public class ModelMapperFacade {

    private ModelMapper modelMapper;

    private RepositoryFactory repositoryFactory;

    public ModelMapperFacade(ModelMapper modelMapper, RepositoryFactory repositoryFactory) {
        this.modelMapper = modelMapper;
        this.repositoryFactory = repositoryFactory;
    }

    public <T> T map(Object data, Class<T> destinationType) {
        T target = modelMapper.map(data, destinationType);

        return refreshReferences(data, target);
    }

    public <T> Page<T> map(Page<?> dataPage, Class<T> destinationType) {
        return dataPage.map(data -> map(data, destinationType));
    }

    public <T> List<T> map(List<?> dataList, Class<T> destinationType) {
        return dataList.stream().map(data -> map(data, destinationType)).collect(Collectors.toList());
    }

    private <T> T refreshReferences(Object data, T target) {
        Field[] fields = data.getClass().getDeclaredFields();

        for (Field field : fields) {
            IdReference idReference = field.getAnnotation(IdReference.class);

            if (idReference != null) {
                field.setAccessible(true);

                try {
                    SimpleJpaRepository<?, Long> repository = repositoryFactory.create(idReference.target());
                    Field fieldTarget = target.getClass().getDeclaredField(idReference.property());
                    fieldTarget.setAccessible(true);

                    Object fieldValue = field.get(data);

                    if (fieldValue == null) {
                        continue;
                    }

                    if (field.get(data) instanceof List) {
                        fieldTarget.set(target, repository.findAllById(FieldUtils.getLongValues(data, field)));
                    } else if (field.get(data) instanceof Set) {
                        fieldTarget.set(target, new HashSet<>(repository.findAllById(FieldUtils.getLongValues(data, field))));
                    } else {
                        Optional<?> value = repository.findById((Long) fieldValue);

                        if (!value.isPresent()) {
                            throw new InformationNotFoundException(fieldTarget.getName());
                        }

                        fieldTarget.set(target, value.get());
                    }
                } catch (NoSuchFieldException | IllegalAccessException exception) {
                    throw new IdReferenceException(exception);
                }
            }
        }

        return target;
    }

}
