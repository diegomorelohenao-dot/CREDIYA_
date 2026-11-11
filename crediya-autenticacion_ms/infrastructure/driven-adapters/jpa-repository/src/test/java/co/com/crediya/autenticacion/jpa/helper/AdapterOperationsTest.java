package co.com.crediya.autenticacion.jpa.helper;

import co.com.crediya.autenticacion.jpa.JPARepository;
import co.com.crediya.autenticacion.jpa.JPARepositoryAdapter;
import co.com.crediya.autenticacion.jpa.entity.UsuarioEntity;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    private JPARepository repository;
    private ObjectMapper objectMapper;
    private JPARepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(JPARepository.class);
        objectMapper = mock(ObjectMapper.class);
        adapter = new JPARepositoryAdapter(repository, objectMapper);
    }

    @Test
    void shouldSaveAndMapUser() {
        Usuario usuario = Usuario.builder().nombres("Ana").build();
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(1L);

        when(objectMapper.map(any(Usuario.class), eq(UsuarioEntity.class))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(objectMapper.mapBuilder(entity, Usuario.UsuarioBuilder.class))
                .thenReturn(Usuario.builder().id(1L).nombres("Ana"));

        Usuario saved = adapter.save(usuario);

        assertEquals(1L, saved.getId());
        assertEquals("Ana", saved.getNombres());
    }

    @Test
    void shouldCheckIfEmailIsAlreadyRegistered() {
        when(repository.existsByCorreoElectronicoIgnoreCase("ana@example.com")).thenReturn(true);

        assertTrue(adapter.existsByCorreoElectronico("ana@example.com"));
    }
}
