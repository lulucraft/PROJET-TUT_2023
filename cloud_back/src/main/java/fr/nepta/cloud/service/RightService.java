package fr.nepta.cloud.service;

import java.util.Collection;

import fr.nepta.cloud.model.Right;

public interface RightService {

	Right getRight(String name);

	Right getRight(long rightId);

	Right saveRight(Right right);

	Collection<Right> getRights();


}
