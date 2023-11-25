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
import model.Pessoa;
import model.Produto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.PessoaJuridica;

/**
 *
 * @author JPZanirati
 */
public class PessoaJuridicaJpaController implements Serializable {

    public PessoaJuridicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PessoaJuridica pessoaJuridica) throws PreexistingEntityException, Exception {
        if (pessoaJuridica.getProdutoCollection() == null) {
            pessoaJuridica.setProdutoCollection(new ArrayList<Produto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa idPessoa = pessoaJuridica.getIdPessoa();
            if (idPessoa != null) {
                idPessoa = em.getReference(idPessoa.getClass(), idPessoa.getIdPessoa());
                pessoaJuridica.setIdPessoa(idPessoa);
            }
            Collection<Produto> attachedProdutoCollection = new ArrayList<Produto>();
            for (Produto produtoCollectionProdutoToAttach : pessoaJuridica.getProdutoCollection()) {
                produtoCollectionProdutoToAttach = em.getReference(produtoCollectionProdutoToAttach.getClass(), produtoCollectionProdutoToAttach.getIdProduto());
                attachedProdutoCollection.add(produtoCollectionProdutoToAttach);
            }
            pessoaJuridica.setProdutoCollection(attachedProdutoCollection);
            em.persist(pessoaJuridica);
            if (idPessoa != null) {
                idPessoa.getPessoaJuridicaCollection().add(pessoaJuridica);
                idPessoa = em.merge(idPessoa);
            }
            for (Produto produtoCollectionProduto : pessoaJuridica.getProdutoCollection()) {
                PessoaJuridica oldIdJuridicaOfProdutoCollectionProduto = produtoCollectionProduto.getIdJuridica();
                produtoCollectionProduto.setIdJuridica(pessoaJuridica);
                produtoCollectionProduto = em.merge(produtoCollectionProduto);
                if (oldIdJuridicaOfProdutoCollectionProduto != null) {
                    oldIdJuridicaOfProdutoCollectionProduto.getProdutoCollection().remove(produtoCollectionProduto);
                    oldIdJuridicaOfProdutoCollectionProduto = em.merge(oldIdJuridicaOfProdutoCollectionProduto);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoaJuridica(pessoaJuridica.getIdJuridica()) != null) {
                throw new PreexistingEntityException("PessoaJuridica " + pessoaJuridica + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PessoaJuridica pessoaJuridica) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PessoaJuridica persistentPessoaJuridica = em.find(PessoaJuridica.class, pessoaJuridica.getIdJuridica());
            Pessoa idPessoaOld = persistentPessoaJuridica.getIdPessoa();
            Pessoa idPessoaNew = pessoaJuridica.getIdPessoa();
            Collection<Produto> produtoCollectionOld = persistentPessoaJuridica.getProdutoCollection();
            Collection<Produto> produtoCollectionNew = pessoaJuridica.getProdutoCollection();
            List<String> illegalOrphanMessages = null;
            for (Produto produtoCollectionOldProduto : produtoCollectionOld) {
                if (!produtoCollectionNew.contains(produtoCollectionOldProduto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Produto " + produtoCollectionOldProduto + " since its idJuridica field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idPessoaNew != null) {
                idPessoaNew = em.getReference(idPessoaNew.getClass(), idPessoaNew.getIdPessoa());
                pessoaJuridica.setIdPessoa(idPessoaNew);
            }
            Collection<Produto> attachedProdutoCollectionNew = new ArrayList<Produto>();
            for (Produto produtoCollectionNewProdutoToAttach : produtoCollectionNew) {
                produtoCollectionNewProdutoToAttach = em.getReference(produtoCollectionNewProdutoToAttach.getClass(), produtoCollectionNewProdutoToAttach.getIdProduto());
                attachedProdutoCollectionNew.add(produtoCollectionNewProdutoToAttach);
            }
            produtoCollectionNew = attachedProdutoCollectionNew;
            pessoaJuridica.setProdutoCollection(produtoCollectionNew);
            pessoaJuridica = em.merge(pessoaJuridica);
            if (idPessoaOld != null && !idPessoaOld.equals(idPessoaNew)) {
                idPessoaOld.getPessoaJuridicaCollection().remove(pessoaJuridica);
                idPessoaOld = em.merge(idPessoaOld);
            }
            if (idPessoaNew != null && !idPessoaNew.equals(idPessoaOld)) {
                idPessoaNew.getPessoaJuridicaCollection().add(pessoaJuridica);
                idPessoaNew = em.merge(idPessoaNew);
            }
            for (Produto produtoCollectionNewProduto : produtoCollectionNew) {
                if (!produtoCollectionOld.contains(produtoCollectionNewProduto)) {
                    PessoaJuridica oldIdJuridicaOfProdutoCollectionNewProduto = produtoCollectionNewProduto.getIdJuridica();
                    produtoCollectionNewProduto.setIdJuridica(pessoaJuridica);
                    produtoCollectionNewProduto = em.merge(produtoCollectionNewProduto);
                    if (oldIdJuridicaOfProdutoCollectionNewProduto != null && !oldIdJuridicaOfProdutoCollectionNewProduto.equals(pessoaJuridica)) {
                        oldIdJuridicaOfProdutoCollectionNewProduto.getProdutoCollection().remove(produtoCollectionNewProduto);
                        oldIdJuridicaOfProdutoCollectionNewProduto = em.merge(oldIdJuridicaOfProdutoCollectionNewProduto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoaJuridica.getIdJuridica();
                if (findPessoaJuridica(id) == null) {
                    throw new NonexistentEntityException("The pessoaJuridica with id " + id + " no longer exists.");
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
            PessoaJuridica pessoaJuridica;
            try {
                pessoaJuridica = em.getReference(PessoaJuridica.class, id);
                pessoaJuridica.getIdJuridica();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoaJuridica with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Produto> produtoCollectionOrphanCheck = pessoaJuridica.getProdutoCollection();
            for (Produto produtoCollectionOrphanCheckProduto : produtoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This PessoaJuridica (" + pessoaJuridica + ") cannot be destroyed since the Produto " + produtoCollectionOrphanCheckProduto + " in its produtoCollection field has a non-nullable idJuridica field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Pessoa idPessoa = pessoaJuridica.getIdPessoa();
            if (idPessoa != null) {
                idPessoa.getPessoaJuridicaCollection().remove(pessoaJuridica);
                idPessoa = em.merge(idPessoa);
            }
            em.remove(pessoaJuridica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PessoaJuridica> findPessoaJuridicaEntities() {
        return findPessoaJuridicaEntities(true, -1, -1);
    }

    public List<PessoaJuridica> findPessoaJuridicaEntities(int maxResults, int firstResult) {
        return findPessoaJuridicaEntities(false, maxResults, firstResult);
    }

    private List<PessoaJuridica> findPessoaJuridicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PessoaJuridica.class));
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

    public PessoaJuridica findPessoaJuridica(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PessoaJuridica.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaJuridicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PessoaJuridica> rt = cq.from(PessoaJuridica.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
