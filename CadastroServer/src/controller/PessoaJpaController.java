/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Usuario;
import model.PessoaFisica;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Pessoa;
import model.PessoaJuridica;

/**
 *
 * @author JPZanirati
 */
public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) throws PreexistingEntityException, Exception {
        if (pessoa.getPessoaFisicaCollection() == null) {
            pessoa.setPessoaFisicaCollection(new ArrayList<PessoaFisica>());
        }
        if (pessoa.getPessoaJuridicaCollection() == null) {
            pessoa.setPessoaJuridicaCollection(new ArrayList<PessoaJuridica>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUser = pessoa.getIdUser();
            if (idUser != null) {
                idUser = em.getReference(idUser.getClass(), idUser.getIdUser());
                pessoa.setIdUser(idUser);
            }
            Collection<PessoaFisica> attachedPessoaFisicaCollection = new ArrayList<PessoaFisica>();
            for (PessoaFisica pessoaFisicaCollectionPessoaFisicaToAttach : pessoa.getPessoaFisicaCollection()) {
                pessoaFisicaCollectionPessoaFisicaToAttach = em.getReference(pessoaFisicaCollectionPessoaFisicaToAttach.getClass(), pessoaFisicaCollectionPessoaFisicaToAttach.getIdFisica());
                attachedPessoaFisicaCollection.add(pessoaFisicaCollectionPessoaFisicaToAttach);
            }
            pessoa.setPessoaFisicaCollection(attachedPessoaFisicaCollection);
            Collection<PessoaJuridica> attachedPessoaJuridicaCollection = new ArrayList<PessoaJuridica>();
            for (PessoaJuridica pessoaJuridicaCollectionPessoaJuridicaToAttach : pessoa.getPessoaJuridicaCollection()) {
                pessoaJuridicaCollectionPessoaJuridicaToAttach = em.getReference(pessoaJuridicaCollectionPessoaJuridicaToAttach.getClass(), pessoaJuridicaCollectionPessoaJuridicaToAttach.getIdJuridica());
                attachedPessoaJuridicaCollection.add(pessoaJuridicaCollectionPessoaJuridicaToAttach);
            }
            pessoa.setPessoaJuridicaCollection(attachedPessoaJuridicaCollection);
            em.persist(pessoa);
            if (idUser != null) {
                idUser.getPessoaCollection().add(pessoa);
                idUser = em.merge(idUser);
            }
            for (PessoaFisica pessoaFisicaCollectionPessoaFisica : pessoa.getPessoaFisicaCollection()) {
                Pessoa oldIdPessoaOfPessoaFisicaCollectionPessoaFisica = pessoaFisicaCollectionPessoaFisica.getIdPessoa();
                pessoaFisicaCollectionPessoaFisica.setIdPessoa(pessoa);
                pessoaFisicaCollectionPessoaFisica = em.merge(pessoaFisicaCollectionPessoaFisica);
                if (oldIdPessoaOfPessoaFisicaCollectionPessoaFisica != null) {
                    oldIdPessoaOfPessoaFisicaCollectionPessoaFisica.getPessoaFisicaCollection().remove(pessoaFisicaCollectionPessoaFisica);
                    oldIdPessoaOfPessoaFisicaCollectionPessoaFisica = em.merge(oldIdPessoaOfPessoaFisicaCollectionPessoaFisica);
                }
            }
            for (PessoaJuridica pessoaJuridicaCollectionPessoaJuridica : pessoa.getPessoaJuridicaCollection()) {
                Pessoa oldIdPessoaOfPessoaJuridicaCollectionPessoaJuridica = pessoaJuridicaCollectionPessoaJuridica.getIdPessoa();
                pessoaJuridicaCollectionPessoaJuridica.setIdPessoa(pessoa);
                pessoaJuridicaCollectionPessoaJuridica = em.merge(pessoaJuridicaCollectionPessoaJuridica);
                if (oldIdPessoaOfPessoaJuridicaCollectionPessoaJuridica != null) {
                    oldIdPessoaOfPessoaJuridicaCollectionPessoaJuridica.getPessoaJuridicaCollection().remove(pessoaJuridicaCollectionPessoaJuridica);
                    oldIdPessoaOfPessoaJuridicaCollectionPessoaJuridica = em.merge(oldIdPessoaOfPessoaJuridicaCollectionPessoaJuridica);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoa(pessoa.getIdPessoa()) != null) {
                throw new PreexistingEntityException("Pessoa " + pessoa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getIdPessoa());
            Usuario idUserOld = persistentPessoa.getIdUser();
            Usuario idUserNew = pessoa.getIdUser();
            Collection<PessoaFisica> pessoaFisicaCollectionOld = persistentPessoa.getPessoaFisicaCollection();
            Collection<PessoaFisica> pessoaFisicaCollectionNew = pessoa.getPessoaFisicaCollection();
            Collection<PessoaJuridica> pessoaJuridicaCollectionOld = persistentPessoa.getPessoaJuridicaCollection();
            Collection<PessoaJuridica> pessoaJuridicaCollectionNew = pessoa.getPessoaJuridicaCollection();
            List<String> illegalOrphanMessages = null;
            for (PessoaFisica pessoaFisicaCollectionOldPessoaFisica : pessoaFisicaCollectionOld) {
                if (!pessoaFisicaCollectionNew.contains(pessoaFisicaCollectionOldPessoaFisica)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PessoaFisica " + pessoaFisicaCollectionOldPessoaFisica + " since its idPessoa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUserNew != null) {
                idUserNew = em.getReference(idUserNew.getClass(), idUserNew.getIdUser());
                pessoa.setIdUser(idUserNew);
            }
            Collection<PessoaFisica> attachedPessoaFisicaCollectionNew = new ArrayList<PessoaFisica>();
            for (PessoaFisica pessoaFisicaCollectionNewPessoaFisicaToAttach : pessoaFisicaCollectionNew) {
                pessoaFisicaCollectionNewPessoaFisicaToAttach = em.getReference(pessoaFisicaCollectionNewPessoaFisicaToAttach.getClass(), pessoaFisicaCollectionNewPessoaFisicaToAttach.getIdFisica());
                attachedPessoaFisicaCollectionNew.add(pessoaFisicaCollectionNewPessoaFisicaToAttach);
            }
            pessoaFisicaCollectionNew = attachedPessoaFisicaCollectionNew;
            pessoa.setPessoaFisicaCollection(pessoaFisicaCollectionNew);
            Collection<PessoaJuridica> attachedPessoaJuridicaCollectionNew = new ArrayList<PessoaJuridica>();
            for (PessoaJuridica pessoaJuridicaCollectionNewPessoaJuridicaToAttach : pessoaJuridicaCollectionNew) {
                pessoaJuridicaCollectionNewPessoaJuridicaToAttach = em.getReference(pessoaJuridicaCollectionNewPessoaJuridicaToAttach.getClass(), pessoaJuridicaCollectionNewPessoaJuridicaToAttach.getIdJuridica());
                attachedPessoaJuridicaCollectionNew.add(pessoaJuridicaCollectionNewPessoaJuridicaToAttach);
            }
            pessoaJuridicaCollectionNew = attachedPessoaJuridicaCollectionNew;
            pessoa.setPessoaJuridicaCollection(pessoaJuridicaCollectionNew);
            pessoa = em.merge(pessoa);
            if (idUserOld != null && !idUserOld.equals(idUserNew)) {
                idUserOld.getPessoaCollection().remove(pessoa);
                idUserOld = em.merge(idUserOld);
            }
            if (idUserNew != null && !idUserNew.equals(idUserOld)) {
                idUserNew.getPessoaCollection().add(pessoa);
                idUserNew = em.merge(idUserNew);
            }
            for (PessoaFisica pessoaFisicaCollectionNewPessoaFisica : pessoaFisicaCollectionNew) {
                if (!pessoaFisicaCollectionOld.contains(pessoaFisicaCollectionNewPessoaFisica)) {
                    Pessoa oldIdPessoaOfPessoaFisicaCollectionNewPessoaFisica = pessoaFisicaCollectionNewPessoaFisica.getIdPessoa();
                    pessoaFisicaCollectionNewPessoaFisica.setIdPessoa(pessoa);
                    pessoaFisicaCollectionNewPessoaFisica = em.merge(pessoaFisicaCollectionNewPessoaFisica);
                    if (oldIdPessoaOfPessoaFisicaCollectionNewPessoaFisica != null && !oldIdPessoaOfPessoaFisicaCollectionNewPessoaFisica.equals(pessoa)) {
                        oldIdPessoaOfPessoaFisicaCollectionNewPessoaFisica.getPessoaFisicaCollection().remove(pessoaFisicaCollectionNewPessoaFisica);
                        oldIdPessoaOfPessoaFisicaCollectionNewPessoaFisica = em.merge(oldIdPessoaOfPessoaFisicaCollectionNewPessoaFisica);
                    }
                }
            }
            for (PessoaJuridica pessoaJuridicaCollectionOldPessoaJuridica : pessoaJuridicaCollectionOld) {
                if (!pessoaJuridicaCollectionNew.contains(pessoaJuridicaCollectionOldPessoaJuridica)) {
                    pessoaJuridicaCollectionOldPessoaJuridica.setIdPessoa(null);
                    pessoaJuridicaCollectionOldPessoaJuridica = em.merge(pessoaJuridicaCollectionOldPessoaJuridica);
                }
            }
            for (PessoaJuridica pessoaJuridicaCollectionNewPessoaJuridica : pessoaJuridicaCollectionNew) {
                if (!pessoaJuridicaCollectionOld.contains(pessoaJuridicaCollectionNewPessoaJuridica)) {
                    Pessoa oldIdPessoaOfPessoaJuridicaCollectionNewPessoaJuridica = pessoaJuridicaCollectionNewPessoaJuridica.getIdPessoa();
                    pessoaJuridicaCollectionNewPessoaJuridica.setIdPessoa(pessoa);
                    pessoaJuridicaCollectionNewPessoaJuridica = em.merge(pessoaJuridicaCollectionNewPessoaJuridica);
                    if (oldIdPessoaOfPessoaJuridicaCollectionNewPessoaJuridica != null && !oldIdPessoaOfPessoaJuridicaCollectionNewPessoaJuridica.equals(pessoa)) {
                        oldIdPessoaOfPessoaJuridicaCollectionNewPessoaJuridica.getPessoaJuridicaCollection().remove(pessoaJuridicaCollectionNewPessoaJuridica);
                        oldIdPessoaOfPessoaJuridicaCollectionNewPessoaJuridica = em.merge(oldIdPessoaOfPessoaJuridicaCollectionNewPessoaJuridica);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoa.getIdPessoa();
                if (findPessoa(id) == null) {
                    throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getIdPessoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<PessoaFisica> pessoaFisicaCollectionOrphanCheck = pessoa.getPessoaFisicaCollection();
            for (PessoaFisica pessoaFisicaCollectionOrphanCheckPessoaFisica : pessoaFisicaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pessoa (" + pessoa + ") cannot be destroyed since the PessoaFisica " + pessoaFisicaCollectionOrphanCheckPessoaFisica + " in its pessoaFisicaCollection field has a non-nullable idPessoa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUser = pessoa.getIdUser();
            if (idUser != null) {
                idUser.getPessoaCollection().remove(pessoa);
                idUser = em.merge(idUser);
            }
            Collection<PessoaJuridica> pessoaJuridicaCollection = pessoa.getPessoaJuridicaCollection();
            for (PessoaJuridica pessoaJuridicaCollectionPessoaJuridica : pessoaJuridicaCollection) {
                pessoaJuridicaCollectionPessoaJuridica.setIdPessoa(null);
                pessoaJuridicaCollectionPessoaJuridica = em.merge(pessoaJuridicaCollectionPessoaJuridica);
            }
            em.remove(pessoa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
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

    public Pessoa findPessoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
