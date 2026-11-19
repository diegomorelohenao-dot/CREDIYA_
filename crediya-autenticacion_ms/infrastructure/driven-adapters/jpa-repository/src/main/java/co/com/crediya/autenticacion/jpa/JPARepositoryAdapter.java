package co.com.crediya.autenticacion.jpa;

import co.com.crediya.autenticacion.jpa.entity.UsuarioEntity;
import co.com.crediya.autenticacion.jpa.helper.AdapterOperations;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exceptions.UsuarioYaRegistradoException;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class JPARepositoryAdapter
        extends AdapterOperations<Usuario, UsuarioEntity, Long, JPARepository>
        implements UsuarioRepository {

    public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
        super(repository, mapper,
                entity -> mapper.mapBuilder(entity, Usuario.UsuarioBuilder.class).build());
    }

    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return repository.existsByCorreoElectronicoIgnoreCase(correoElectronico);
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return repository.existsByDocumento(documento);
    }

    @Override
    public Optional<Usuario> findByCorreoElectronico(String correoElectronico) {
        return repository.findByCorreoElectronicoIgnoreCase(correoElectronico)
                .map(entity -> mapper.mapBuilder(entity, Usuario.UsuarioBuilder.class).build());
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        try {
            return super.save(usuario);
        } catch (DataIntegrityViolationException exception) {
            throw new UsuarioYaRegistradoException();
        }
    }
}