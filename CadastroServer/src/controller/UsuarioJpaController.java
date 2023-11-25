/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Pessoa;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Usuario;

/**
 *
 * @author JPZanirati
 */

public class UsuarioJpaController implements Serializable {

    private EntityManager em = null;
    
    public UsuarioJpaController(EntityManager em) {
        this.em = em;
    }

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getPessoaCollection() == null) {
            usuario.setPessoaCollection(new ArrayList<>());
        }
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Pessoa> attachedPessoaCollection = new ArrayList<>();
            for (Pessoa pessoaCollectionPessoaToAttach : usuario.getPessoaCollection()) {
                pessoaCollectionPessoaToAttach = em.getReference(pessoaCollectionPessoaToAttach.getClass(), pessoaCollectionPessoaToAttach.getIdPessoa());
                attachedPessoaCollection.add(pessoaCollectionPessoaToAttach);
            }
            usuario.setPessoaCollection(attachedPessoaCollection);
            em.persist(usuario);
            for (Pessoa pessoaCollectionPessoa : usuario.getPessoaCollection()) {
                Usuario oldIdUserOfPessoaCollectionPessoa = pessoaCollectionPessoa.getIdUser();
                pessoaCollectionPessoa.setIdUser(usuario);
                pessoaCollectionPessoa = em.merge(pessoaCollectionPessoa);
                if (oldIdUserOfPessoaCollectionPessoa != null) {
                    oldIdUserOfPessoaCollectionPessoa.getPessoaCollection().remove(pessoaCollectionPessoa);
                    oldIdUserOfPessoaCollectionPessoa = em.merge(oldIdUserOfPessoaCollectionPessoa);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuarioId(usuario.getIdUser()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws NonexistentEntityException, Exception {
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUser());
            Collection<Pessoa> pessoaCollectionOld = persistentUsuario.getPessoaCollection();
            Collection<Pessoa> pessoaCollectionNew = usuario.getPessoaCollection();
            Collection<Pessoa> attachedPessoaCollectionNew = new ArrayList<>();
            for (Pessoa pessoaCollectionNewPessoaToAttach : pessoaCollectionNew) {
                pessoaCollectionNewPessoaToAttach = em.getReference(pessoaCollectionNewPessoaToAttach.getClass(), pessoaCollectionNewPessoaToAttach.getIdPessoa());
                attachedPessoaCollectionNew.add(pessoaCollectionNewPessoaToAttach);
            }
            pessoaCollectionNew = attachedPessoaCollectionNew;
            usuario.setPessoaCollection(pessoaCollectionNew);
            usuario = em.merge(usuario);
            for (Pessoa pessoaCollectionOldPessoa : pessoaCollectionOld) {
                if (!pessoaCollectionNew.contains(pessoaCollectionOldPessoa)) {
                    pessoaCollectionOldPessoa.setIdUser(null);
                    pessoaCollectionOldPessoa = em.merge(pessoaCollectionOldPessoa);
                }
            }
            for (Pessoa pessoaCollectionNewPessoa : pessoaCollectionNew) {
                if (!pessoaCollectionOld.contains(pessoaCollectionNewPessoa)) {
                    Usuario oldIdUserOfPessoaCollectionNewPessoa = pessoaCollectionNewPessoa.getIdUser();
                    pessoaCollectionNewPessoa.setIdUser(usuario);
                    pessoaCollectionNewPessoa = em.merge(pessoaCollectionNewPessoa);
                    if (oldIdUserOfPessoaCollectionNewPessoa != null && !oldIdUserOfPessoaCollectionNewPessoa.equals(usuario)) {
                        oldIdUserOfPessoaCollectionNewPessoa.getPessoaCollection().remove(pessoaCollectionNewPessoa);
                        oldIdUserOfPessoaCollectionNewPessoa = em.merge(oldIdUserOfPessoaCollectionNewPessoa);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdUser();
                if (findUsuarioId(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUser();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            Collection<Pessoa> pessoaCollection = usuario.getPessoaCollection();
            for (Pessoa pessoaCollectionPessoa : pessoaCollection) {
                pessoaCollectionPessoa.setIdUser(null);
                pessoaCollectionPessoa = em.merge(pessoaCollectionPessoa);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

public Usuario findUsuario(String login, String senha, Integer id) {
    try {
        StringBuilder queryString = new StringBuilder("SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha");
        if (id != null) {
            queryString.append(" AND u.id = :id");
        }

        Query query = em.createQuery(queryString.toString());
        query.setParameter("login", login);
        query.setParameter("senha", senha);

        if (id != null) {
            query.setParameter("id", id);
        }

        List<Usuario> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    } finally {
        if (em != null) {
            em.close();
        }
    }
}
    public Usuario findUsuarioId(Integer id) {
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
