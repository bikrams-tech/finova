package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Company;
import jakarta.persistence.EntityManager;

import java.util.List;

public abstract class BaseRepository<T> {

    private final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(EntityManager em, T entity) {
        em.persist(entity);
        return entity;
    }

    public T update(EntityManager em, T entity) {
        return em.merge(entity);
    }


    public void delete(EntityManager em, T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
    public void deleteById(EntityManager em, Long id){
        T entity = em.find(entityClass,id);
        if(entity != null){
            em.remove(entity);
        }
    }

    public T findById(EntityManager em, Long id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll(EntityManager em) {
        return em.createQuery(
                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                entityClass
        ).getResultList();
    }

    public List<T> findAllByCompany(EntityManager em, Long companyId) {

        String jpql =
                "SELECT e FROM " + entityClass.getSimpleName() +
                        " e WHERE e.company.id = :companyId";

        return em.createQuery(jpql, entityClass)
                .setParameter("companyId", companyId)
                .getResultList();
    }

    public boolean existsById(EntityManager em, Long id){
        return em.find(entityClass, id) != null;
    }

    public T findByName(EntityManager em, String name) {

        String jpql =
                "SELECT e FROM " + entityClass.getSimpleName() +
                        " e WHERE LOWER(e.name) = LOWER(:name)";

        List<T> result = em.createQuery(jpql, entityClass)
                .setParameter("name", name)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public List<T> findPage(EntityManager em,int page,int size){

        String jpql =
                "SELECT e FROM " + entityClass.getSimpleName() + " e";

        return em.createQuery(jpql,entityClass)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long count(EntityManager em){

        String jpql =
                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";

        return em.createQuery(jpql,Long.class)
                .getSingleResult();
    }
    public long countByCompany(EntityManager em, Long companyId) {

        String jpql = String.format(
                "SELECT COUNT(e) FROM %s e WHERE e.company.id = :companyId",
                entityClass.getSimpleName()
        );

        return em.createQuery(jpql, Long.class)
                .setParameter("companyId", companyId)
                .getSingleResult();
    }
    public List<T> searchByName(EntityManager em, String keyword) {

        String jpql =
                "SELECT e FROM " + entityClass.getSimpleName() +
                        " e WHERE LOWER(e.name) LIKE LOWER(:keyword)";

        return em.createQuery(jpql, entityClass)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }
    public T findByNameAndCompany(EntityManager em, String name, Long companyId){

        String jpql =
                "SELECT e FROM " + entityClass.getSimpleName() +
                        " e WHERE LOWER(e.name) = LOWER(:name) " +
                        "AND e.company.id = :companyId";

        List<T> result = em.createQuery(jpql, entityClass)
                .setParameter("name", name)
                .setParameter("companyId", companyId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}