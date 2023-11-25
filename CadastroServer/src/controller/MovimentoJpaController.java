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
import model.PessoaFisica;
import model.ProdutoMovimento;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Movimento;

/**
 *
 * @author JPZanirati
 */
public class MovimentoJpaController implements Serializable {

    public MovimentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimento movimento) throws PreexistingEntityException, Exception {
        if (movimento.getProdutoMovimentoCollection() == null) {
            movimento.setProdutoMovimentoCollection(new ArrayList<ProdutoMovimento>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PessoaFisica idFisica = movimento.getIdFisica();
            if (idFisica != null) {
                idFisica = em.getReference(idFisica.getClass(), idFisica.getIdFisica());
                movimento.setIdFisica(idFisica);
            }
            Collection<ProdutoMovimento> attachedProdutoMovimentoCollection = new ArrayList<ProdutoMovimento>();
            for (ProdutoMovimento produtoMovimentoCollectionProdutoMovimentoToAttach : movimento.getProdutoMovimentoCollection()) {
                produtoMovimentoCollectionProdutoMovimentoToAttach = em.getReference(produtoMovimentoCollectionProdutoMovimentoToAttach.getClass(), produtoMovimentoCollectionProdutoMovimentoToAttach.getIdPM());
                attachedProdutoMovimentoCollection.add(produtoMovimentoCollectionProdutoMovimentoToAttach);
            }
            movimento.setProdutoMovimentoCollection(attachedProdutoMovimentoCollection);
            em.persist(movimento);
            if (idFisica != null) {
                idFisica.getMovimentoCollection().add(movimento);
                idFisica = em.merge(idFisica);
            }
            for (ProdutoMovimento produtoMovimentoCollectionProdutoMovimento : movimento.getProdutoMovimentoCollection()) {
                Movimento oldIdMovimentoOfProdutoMovimentoCollectionProdutoMovimento = produtoMovimentoCollectionProdutoMovimento.getIdMovimento();
                produtoMovimentoCollectionProdutoMovimento.setIdMovimento(movimento);
                produtoMovimentoCollectionProdutoMovimento = em.merge(produtoMovimentoCollectionProdutoMovimento);
                if (oldIdMovimentoOfProdutoMovimentoCollectionProdutoMovimento != null) {
                    oldIdMovimentoOfProdutoMovimentoCollectionProdutoMovimento.getProdutoMovimentoCollection().remove(produtoMovimentoCollectionProdutoMovimento);
                    oldIdMovimentoOfProdutoMovimentoCollectionProdutoMovimento = em.merge(oldIdMovimentoOfProdutoMovimentoCollectionProdutoMovimento);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimento(movimento.getIdMovimento()) != null) {
                throw new PreexistingEntityException("Movimento " + movimento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movimento movimento) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento persistentMovimento = em.find(Movimento.class, movimento.getIdMovimento());
            PessoaFisica idFisicaOld = persistentMovimento.getIdFisica();
            PessoaFisica idFisicaNew = movimento.getIdFisica();
            Collection<ProdutoMovimento> produtoMovimentoCollectionOld = persistentMovimento.getProdutoMovimentoCollection();
            Collection<ProdutoMovimento> produtoMovimentoCollectionNew = movimento.getProdutoMovimentoCollection();
            List<String> illegalOrphanMessages = null;
            for (ProdutoMovimento produtoMovimentoCollectionOldProdutoMovimento : produtoMovimentoCollectionOld) {
                if (!produtoMovimentoCollectionNew.contains(produtoMovimentoCollectionOldProdutoMovimento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProdutoMovimento " + produtoMovimentoCollectionOldProdutoMovimento + " since its idMovimento field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idFisicaNew != null) {
                idFisicaNew = em.getReference(idFisicaNew.getClass(), idFisicaNew.getIdFisica());
                movimento.setIdFisica(idFisicaNew);
            }
            Collection<ProdutoMovimento> attachedProdutoMovimentoCollectionNew = new ArrayList<ProdutoMovimento>();
            for (ProdutoMovimento produtoMovimentoCollectionNewProdutoMovimentoToAttach : produtoMovimentoCollectionNew) {
                produtoMovimentoCollectionNewProdutoMovimentoToAttach = em.getReference(produtoMovimentoCollectionNewProdutoMovimentoToAttach.getClass(), produtoMovimentoCollectionNewProdutoMovimentoToAttach.getIdPM());
                attachedProdutoMovimentoCollectionNew.add(produtoMovimentoCollectionNewProdutoMovimentoToAttach);
            }
            produtoMovimentoCollectionNew = attachedProdutoMovimentoCollectionNew;
            movimento.setProdutoMovimentoCollection(produtoMovimentoCollectionNew);
            movimento = em.merge(movimento);
            if (idFisicaOld != null && !idFisicaOld.equals(idFisicaNew)) {
                idFisicaOld.getMovimentoCollection().remove(movimento);
                idFisicaOld = em.merge(idFisicaOld);
            }
            if (idFisicaNew != null && !idFisicaNew.equals(idFisicaOld)) {
                idFisicaNew.getMovimentoCollection().add(movimento);
                idFisicaNew = em.merge(idFisicaNew);
            }
            for (ProdutoMovimento produtoMovimentoCollectionNewProdutoMovimento : produtoMovimentoCollectionNew) {
                if (!produtoMovimentoCollectionOld.contains(produtoMovimentoCollectionNewProdutoMovimento)) {
                    Movimento oldIdMovimentoOfProdutoMovimentoCollectionNewProdutoMovimento = produtoMovimentoCollectionNewProdutoMovimento.getIdMovimento();
                    produtoMovimentoCollectionNewProdutoMovimento.setIdMovimento(movimento);
                    produtoMovimentoCollectionNewProdutoMovimento = em.merge(produtoMovimentoCollectionNewProdutoMovimento);
                    if (oldIdMovimentoOfProdutoMovimentoCollectionNewProdutoMovimento != null && !oldIdMovimentoOfProdutoMovimentoCollectionNewProdutoMovimento.equals(movimento)) {
                        oldIdMovimentoOfProdutoMovimentoCollectionNewProdutoMovimento.getProdutoMovimentoCollection().remove(produtoMovimentoCollectionNewProdutoMovimento);
                        oldIdMovimentoOfProdutoMovimentoCollectionNewProdutoMovimento = em.merge(oldIdMovimentoOfProdutoMovimentoCollectionNewProdutoMovimento);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movimento.getIdMovimento();
                if (findMovimento(id) == null) {
                    throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.");
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
            Movimento movimento;
            try {
                movimento = em.getReference(Movimento.class, id);
                movimento.getIdMovimento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<ProdutoMovimento> produtoMovimentoCollectionOrphanCheck = movimento.getProdutoMovimentoCollection();
            for (ProdutoMovimento produtoMovimentoCollectionOrphanCheckProdutoMovimento : produtoMovimentoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movimento (" + movimento + ") cannot be destroyed since the ProdutoMovimento " + produtoMovimentoCollectionOrphanCheckProdutoMovimento + " in its produtoMovimentoCollection field has a non-nullable idMovimento field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            PessoaFisica idFisica = movimento.getIdFisica();
            if (idFisica != null) {
                idFisica.getMovimentoCollection().remove(movimento);
                idFisica = em.merge(idFisica);
            }
            em.remove(movimento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movimento> findMovimentoEntities() {
        return findMovimentoEntities(true, -1, -1);
    }

    public List<Movimento> findMovimentoEntities(int maxResults, int firstResult) {
        return findMovimentoEntities(false, maxResults, firstResult);
    }

    private List<Movimento> findMovimentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movimento.class));
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

    public Movimento findMovimento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimento.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movimento> rt = cq.from(Movimento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
