package com.basic.flowable.model;

import com.basic.flowable.utils.UUIDGenerator;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanbin
 * @description TODO (配置项)
 * @date 2023/10/19
 */
@Component
public class FlowableModelRepositoryImpl implements ModelRepository {

    private static final String NAMESPACE = "org.flowable.ui.modeler.domain.Model.";

    @Autowired
    @Qualifier("flowableModeler")
    protected SqlSessionTemplate sqlSessionTemplate;

    @Override
    public Model get(String id) {
        return sqlSessionTemplate.selectOne(NAMESPACE + "selectModel", id);
    }

    @Override
    public List<Model> findByModelType(Integer modelType, String sort) {
        Map<String, Object> params = new HashMap<>();
        params.put("modelType", modelType);
        params.put("sort", sort);
        return findModelsByParameters(params);
    }

    @Override
    public List<Model> findByModelTypeAndFilter(Integer modelType, String filter, String sort) {
        Map<String, Object> params = new HashMap<>();
        params.put("modelType", modelType);
        params.put("filter", filter);
        params.put("sort", sort);
        return findModelsByParameters(params);
    }

    @Override
    public List<Model> findByKeyAndType(String key, Integer modelType) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("modelType", modelType);
        return findModelsByParameters(params);
    }

    @Override
    public List<Model> findByParentModelId(String parentModelId) {
        return sqlSessionTemplate.selectList(NAMESPACE + "selectModelByParentModelId", parentModelId);
    }

    @Override
    public Long countByModelTypeAndCreatedBy(int modelType, String createdBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("createdBy", createdBy);
        params.put("modelType", modelType);
        return sqlSessionTemplate.selectOne(NAMESPACE + "countByModelTypeAndCreatedBy", params);
    }

    protected List<Model> findModelsByParameters(Map<String, Object> parameters) {
        return sqlSessionTemplate.selectList(NAMESPACE + "selectModelByParameters", parameters);
    }

    @Override
    public void save(Model model) {
        if (model.getId() == null) {
            model.setId(UUIDGenerator.generate());
            sqlSessionTemplate.insert(NAMESPACE + "insertModel", model);
        } else {
            sqlSessionTemplate.update(NAMESPACE + "updateModel", model);
        }
    }

    @Override
    public void delete(Model model) {
        sqlSessionTemplate.delete(NAMESPACE + "deleteModel", model);
    }
}
