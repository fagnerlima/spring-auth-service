package br.pro.fagnerlima.spring.auth.api.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.pro.fagnerlima.spring.auth.api.domain.shared.AuditedBaseEntity;

public class ServiceTestUtils {

    public static void mockAuthenticationForAuditing(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    public static void assertPage(Page<?> page, int pageSize, int pageNumber, int numberOfElements, int totalPages, int totalElements) {
        assertThat(page.getContent().size()).isEqualTo(numberOfElements);
        assertThat(page.getNumberOfElements()).isEqualTo(numberOfElements);
        assertThat(page.getNumber()).isEqualTo(pageNumber);
        assertThat(page.getSize()).isEqualTo(pageSize);
        assertThat(page.getTotalPages()).isEqualTo(totalPages);
        assertThat(page.getTotalElements()).isEqualTo(totalElements);
    }

    public static void assertPageNoContent(Page<?> page, int pageSize, int pageNumber) {
        assertPage(page, pageSize, pageNumber, 0, 0, 0);
    }

    public static <T extends AuditedBaseEntity> void  assertAuditingFields(T entity, String loggedUsername) {
        assertThat(entity.getCreatedBy()).isEqualTo(loggedUsername);
        assertThat(entity.getCreatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(entity.getUpdatedBy()).isEqualTo(loggedUsername);
        assertThat(entity.getUpdatedAt().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(entity.getUpdatedAt()).isAfterOrEqualTo(entity.getCreatedAt());
    }

}
