/***************************************************************************
 * File:  PojoCompositeListener.java Course materials (23S) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 *
 * Updated by:  Group 3
 *   040982007, Olamide, Owolabi (as from ACSIS)
 *   studentId, Jennifer Acevedocarmona (as from ACSIS)
 *   studentId, Delorme, Keelan (as from ACSIS)
 * 
 */
package acmecollege.entity;

import static java.time.LocalDateTime.now;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@SuppressWarnings("unused")

public class PojoCompositeListener {

	@PrePersist
	public void setCreatedOnDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		pojoBaseComposite.setCreated(now());
		pojoBaseComposite.setUpdated(now());
	}

	@PreUpdate
	public void setUpdatedDate(PojoBaseCompositeKey<?> pojoBaseComposite) {
		pojoBaseComposite.setUpdated(now());
	}

}
