/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author JPZanirati
 */
@Entity
@Table(name = "movimento", catalog = "LOJA", schema = "LOJA")
@NamedQueries({
    @NamedQuery(name = "Movimento.findAll", query = "SELECT m FROM Movimento m"),
    @NamedQuery(name = "Movimento.findByIdMovimento", query = "SELECT m FROM Movimento m WHERE m.idMovimento = :idMovimento"),
    @NamedQuery(name = "Movimento.findByQtdPedido", query = "SELECT m FROM Movimento m WHERE m.qtdPedido = :qtdPedido"),
    @NamedQuery(name = "Movimento.findByPrecoTotal", query = "SELECT m FROM Movimento m WHERE m.precoTotal = :precoTotal")})
public class Movimento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idMovimento")
    private Integer idMovimento;
    @Basic(optional = false)
    @Column(name = "qtd_pedido")
    private int qtdPedido;
    @Basic(optional = false)
    @Column(name = "precoTotal")
    private long precoTotal;
    @JoinColumn(name = "idFisica", referencedColumnName = "idFisica")
    @ManyToOne(optional = false)
    private PessoaFisica idFisica;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idMovimento")
    private Collection<ProdutoMovimento> produtoMovimentoCollection;

    public Movimento() {
    }

    public Movimento(Integer idMovimento) {
        this.idMovimento = idMovimento;
    }

    public Movimento(Integer idMovimento, int qtdPedido, long precoTotal) {
        this.idMovimento = idMovimento;
        this.qtdPedido = qtdPedido;
        this.precoTotal = precoTotal;
    }

    public Integer getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Integer idMovimento) {
        this.idMovimento = idMovimento;
    }

    public int getQtdPedido() {
        return qtdPedido;
    }

    public void setQtdPedido(int qtdPedido) {
        this.qtdPedido = qtdPedido;
    }

    public long getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(long precoTotal) {
        this.precoTotal = precoTotal;
    }

    public PessoaFisica getIdFisica() {
        return idFisica;
    }

    public void setIdFisica(PessoaFisica idFisica) {
        this.idFisica = idFisica;
    }

    public Collection<ProdutoMovimento> getProdutoMovimentoCollection() {
        return produtoMovimentoCollection;
    }

    public void setProdutoMovimentoCollection(Collection<ProdutoMovimento> produtoMovimentoCollection) {
        this.produtoMovimentoCollection = produtoMovimentoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMovimento != null ? idMovimento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movimento)) {
            return false;
        }
        Movimento other = (Movimento) object;
        if ((this.idMovimento == null && other.idMovimento != null) || (this.idMovimento != null && !this.idMovimento.equals(other.idMovimento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Movimento[ idMovimento=" + idMovimento + " ]";
    }
    
}
