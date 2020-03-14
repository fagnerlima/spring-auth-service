package br.pro.fagnerlima.spring.auth.api.application.service;

import org.springframework.stereotype.Service;

import br.pro.fagnerlima.spring.auth.api.application.service.exception.BusinessException;
import br.pro.fagnerlima.spring.auth.api.domain.model.grupo.Grupo;
import br.pro.fagnerlima.spring.auth.api.domain.model.permissao.Permissao;
import br.pro.fagnerlima.spring.auth.api.domain.service.GrupoService;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.BaseRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.persistence.hibernate.repository.GrupoRepository;
import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2UserDetailsService;

@Service
public class GrupoServiceImpl extends BaseServiceImpl<Grupo> implements GrupoService {

    private GrupoRepository grupoRepository;

    public GrupoServiceImpl(OAuth2UserDetailsService userDetailsService, GrupoRepository grupoRepository) {
        super(userDetailsService);
        this.grupoRepository = grupoRepository;
    }

    @Override
    public Grupo save(Grupo grupo) {
        checkSave(grupo);

        return super.save(grupo);
    }

    @Override
    public Grupo update(Long id, Grupo grupo) {
        checkUpdate(id, grupo);

        return super.update(id, grupo);
    }

    @Override
    protected BaseRepository<Grupo> getRepository() {
        return grupoRepository;
    }

    private void checkSave(Grupo grupo) {
        if (grupo.getPermissoes() == null) {
            return;
        }

        if (grupo.getPermissoes().stream().anyMatch(Permissao::isRoot)) {
            throw new BusinessException("grupo.save.permissoes.root");
        }

        if (grupo.getPermissoes().stream().anyMatch(Permissao::isSystem)) {
            throw new BusinessException("grupo.save.permissoes.system");
        }

        if ((!getUsuarioAutenticado().isAdmin() && !getUsuarioAutenticado().isRoot())
                && grupo.getPermissoes().stream().anyMatch(Permissao::isAdmin)) {
            throw new BusinessException("grupo.save.permissoes.admin");
        }
    }

    private void checkUpdate(Long id, Grupo grupo) {
        if (id == Grupo.ID_ROOT) {
            checkUpdateRoot(grupo);
        }

        if (id == Grupo.ID_SYSTEM) {
            checkUpdateSystem(grupo);
        }

        if (id == Grupo.ID_ADMIN) {
            checkUpdateAdmin(grupo);
        }
    }

    private void checkUpdateRoot(Grupo grupo) {
        if (grupo.getPermissoes() == null || grupo.getPermissoes().size() != 1
                || !grupo.getPermissoes().stream().anyMatch(Permissao::isRoot)) {
            throw new BusinessException("grupo.update.root.permissoes");
        }
    }

    private void checkUpdateSystem(Grupo grupo) {
        if (grupo.getPermissoes() == null || grupo.getPermissoes().size() != 1
                || !grupo.getPermissoes().stream().anyMatch(Permissao::isSystem)) {
            throw new BusinessException("grupo.update.system.permissoes");
        }
    }

    private void checkUpdateAdmin(Grupo grupo) {
        if (grupo.getPermissoes() == null || grupo.getPermissoes().size() != 1
                || !grupo.getPermissoes().stream().anyMatch(Permissao::isAdmin)) {
            throw new BusinessException("grupo.update.admin.permissoes");
        }
    }

}
