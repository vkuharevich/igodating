package com.igodating.questionary.util;

import com.igodating.questionary.model.Identifiable;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class ServiceUtils {

    public static <T extends Identifiable<ID>, ID> EntitiesListChange<T, ID> changes(List<T> actualEntities, List<T> newEntities, BiPredicate<T, T> equalsPredicate) {
        List<T> toDelete = new ArrayList<>();
        List<Pair<T, T>> toUpdate = new ArrayList<>();
        List<T> toCreate = new ArrayList<>();

        Map<ID, Boolean> actualEntitiesPresentationMap = new HashMap<>();
        Map<ID, T> actualEntitiesIdMap = new HashMap<>();
        actualEntities.forEach(actualEntity -> {
            actualEntitiesPresentationMap.put(actualEntity.getId(), false);
            actualEntitiesIdMap.put(actualEntity.getId(), actualEntity);
        });


        for (T newEntity : newEntities) {
            ID newEntityId = newEntity.getId();

            if (newEntityId == null) {
                toCreate.add(newEntity);
                continue;
            }

            T actualEntity = actualEntitiesIdMap.get(newEntityId);
            if (actualEntity != null) {
                actualEntitiesPresentationMap.put(actualEntity.getId(), true);
                if (!equalsPredicate.test(newEntity, actualEntity)) {
                    toUpdate.add(Pair.of(actualEntity, newEntity));
                }
            }
        }

        actualEntitiesPresentationMap.forEach((key, value) -> {
            if (!actualEntitiesPresentationMap.get(key)) {
                toDelete.add(actualEntitiesIdMap.get(key));
            }
        });

        return new EntitiesListChange<>(toDelete, toUpdate, toCreate);
    }
}
