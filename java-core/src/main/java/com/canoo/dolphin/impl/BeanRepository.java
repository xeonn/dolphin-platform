package com.canoo.dolphin.impl;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.PresentationModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code BeanRepository} keeps a list of all registered Dolphin Beans and the mapping between OpenDolphin IDs and
 * the associated Dolphin Bean.
 *
 * A new bean needs to be registered with the {@link #registerBean(Object, PresentationModel)} method and can be deleted
 * with the {@link #delete(Object)} method.
 */
// TODO mapDolphinToObject() does not really fit here, we should probably move it to Converters, but first we need to fix scopes
public class BeanRepository {

    private final Map<Object, PresentationModel> objectPmToDolphinPm = new HashMap<>();
    private final Map<String, Object> dolphinIdToObjectPm = new HashMap<>();
    private final Dolphin dolphin;

    public BeanRepository(Dolphin dolphin) {
        this.dolphin = dolphin;
    }

    public boolean isManaged(Object bean) {
        return objectPmToDolphinPm.containsKey(bean);
    }

    public <T> void delete(T bean) {
        final PresentationModel model = objectPmToDolphinPm.remove(bean);
        if (model != null) {
            dolphinIdToObjectPm.remove(model.getId());
            dolphin.remove(model);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> beanClass) {
        final List<T> result = new ArrayList<>();
        final List<PresentationModel> presentationModels = dolphin.findAllPresentationModelsByType(DolphinUtils.getDolphinPresentationModelTypeForClass(beanClass));
        for (PresentationModel model : presentationModels) {
            result.add((T) dolphinIdToObjectPm.get(model.getId()));
        }
        return result;
    }

    public Object getBean(String sourceId) {
        return dolphinIdToObjectPm.get(sourceId);
    }

    public String getDolphinId(Object bean) {
        try {
            return objectPmToDolphinPm.get(bean).getId();
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Only managed Dolphin Beans can be used.");
        }
    }

    public Object mapDolphinToObject(Object value, ClassRepository.FieldType fieldType) {
        return fieldType == ClassRepository.FieldType.DOLPHIN_BEAN? dolphinIdToObjectPm.get(value) : value;
    }

    void registerBean(Object bean, PresentationModel model) {
        objectPmToDolphinPm.put(bean, model);
        dolphinIdToObjectPm.put(model.getId(), bean);

    }
}
