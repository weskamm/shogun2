package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.wps.WpsReference;
import org.springframework.stereotype.Repository;


/**
 * @author Nils Bühner
 */
@Repository("wpsReferenceDao")
public class WpsReferenceDao<E extends WpsReference> extends WpsParameterDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public WpsReferenceDao() {
        super((Class<E>) WpsReference.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected WpsReferenceDao(Class<E> clazz) {
        super(clazz);
    }
}
