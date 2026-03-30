package bikram.businessmanager.service;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.repository.BaseRepository;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class BaseService<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final BaseRepository<T> repository;

    protected BaseService(BaseRepository<T> repository) {
        this.repository = repository;
    }

    protected EntityManager getEntityManager(){
        return JPAUtil.getEmf().createEntityManager();
    }

    public T create(T entity){

        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            repository.save(em,entity);
            tx.commit();
            log.info("sucessfully save {}",entity.getClass().getSimpleName());
            return entity;
        }catch(Exception e){
            if(tx.isActive()) tx.rollback();
            log.error("failed to save {}",entity.getClass().getSimpleName() +" " ,e);
            throw new RuntimeException("creation failed ",e);
        }finally{
            em.close();
        }
    }

    public List<T> getAll(){
        EntityManager em = getEntityManager();
        try{
            return repository.findAll(em);
        }finally{
            em.close();
        }
    }

    public List<T> getAllByCompany(Long companyId){
        EntityManager em = getEntityManager();
        try{
            return repository.findAllByCompany(em,companyId);
        }finally{
            em.close();
        }
    }

    public T getById(Long id){
        EntityManager em = getEntityManager();
        try{
            return repository.findById(em,id);
        }finally{
            em.close();
        }
    }

    public T getByName(String name){
        EntityManager em = getEntityManager();
        try{
            return repository.findByName(em,name);
        }finally{
            em.close();
        }
    }

    public List<T> searchByName(String keyword){
        EntityManager em = getEntityManager();
        try{
            return repository.searchByName(em,keyword);
        }finally{
            em.close();
        }
    }

    public boolean existsById(Long id){
        EntityManager em = getEntityManager();
        try{
            return repository.existsById(em,id);
        }finally{
            em.close();
        }
    }

    public long count(){
        EntityManager em = getEntityManager();
        try{
            return repository.count(em);
        }finally{
            em.close();
        }
    }
    public long countByCompany(Long companyId) {
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.countByCompany(em,companyId);
        } finally {
            em.close();
        }
    }

    public List<T> getPage(int page,int size){
        EntityManager em = getEntityManager();
        try{
            return repository.findPage(em,page,size);
        }finally{
            em.close();
        }
    }

    public void deleteById(Long id){

        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            repository.deleteById(em,id);
            tx.commit();
            log.info("Deleted entity with id {}", id);
        }catch(Exception e){
            if(tx.isActive()) tx.rollback();
            log.error("Delete failed for id {}", id, e);
            throw new RuntimeException(e);
        }finally{
            em.close();
        }
    }

    public T update(T entity){

        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            T updated = repository.update(em,entity);
            tx.commit();
            return updated;
        }catch(Exception e){
            if(tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        }finally{
            em.close();
        }
    }

    public void delete(T entity){

        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            repository.delete(em,entity);
            tx.commit();
        }catch(Exception e){
            if(tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        }finally{
            em.close();
        }
    }
}