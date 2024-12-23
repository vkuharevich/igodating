package com.igodating.commons.utils;

import com.igodating.commons.model.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EntitiesListChange<T extends Identifiable<ID>, ID> {
    private List<T> toDelete;
    private List<Pair<T, T>> oldNewPairToUpdate;
    private List<T> toCreate;
}
