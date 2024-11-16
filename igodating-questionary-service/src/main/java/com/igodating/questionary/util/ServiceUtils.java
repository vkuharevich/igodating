package com.igodating.questionary.util;

import com.igodating.questionary.model.Identifiable;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class ServiceUtils {

    public static <T extends Identifiable<ID>, ID> EntitiesListChange<T, ID> changes(List<T> actualEntities, List<T> newEntities, BiPredicate<T, T> equalsPredicate) {
        List<T> toDelete = new ArrayList<>();
        List<Pair<T, T>> toUpdate = new ArrayList<>();
        List<T> toCreate = new ArrayList<>();

        Map<ID, T> actualEntitiesIdMap = actualEntities.stream().collect(Collectors.toMap(Identifiable::getId, v -> v));

        for (T newEntity : newEntities) {
            ID newEntityId = newEntity.getId();

            if (newEntityId == null) {
                toCreate.add(newEntity);
                continue;
            }

            T actualEntity = actualEntitiesIdMap.get(newEntityId);
            if (actualEntity == null) {
                toDelete.add(newEntity);
                continue;
            }

            if (!equalsPredicate.test(newEntity, actualEntity)) {
                toUpdate.add(Pair.of(actualEntity, newEntity));
            }
        }

        return new EntitiesListChange<>(toDelete, toUpdate, toCreate);
    }
}
