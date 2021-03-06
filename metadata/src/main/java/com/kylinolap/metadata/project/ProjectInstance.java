/*
 * Copyright 2013-2014 eBay Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kylinolap.metadata.project;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.kylinolap.common.persistence.ResourceStore;
import com.kylinolap.common.persistence.RootPersistentEntity;
import com.kylinolap.metadata.model.realization.DataModelRealizationType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Project is a concept in Kylin similar to schema in DBMS
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ProjectInstance extends RootPersistentEntity {

    public static final String DEFAULT_PROJECT_NAME = "DEFAULT";

    @JsonProperty("name")
    private String name;

    @JsonProperty("cubes")
    private List<String> cubes;

    @JsonProperty("tables")
    private Set<String> tables;


    @JsonProperty("owner")
    private String owner;

    @JsonProperty("status")
    private ProjectStatusEnum status;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("last_update_time")
    private String lastUpdateTime;

    @JsonProperty("description")
    private String description;

    @JsonProperty("datamodels")
    private List<ProjectDataModel> dataModels;

    public String getResourcePath() {
        return concatResourcePath(name);
    }

    public static String concatResourcePath(String projectName) {
        return ResourceStore.PROJECT_RESOURCE_ROOT + "/" + projectName + ".json";
    }

    public static String getNormalizedProjectName(String project) {
        if (project == null)
            throw new IllegalStateException("Trying to normalized a project name which is null");

        return project.toUpperCase();
    }

    // ============================================================================

    public static ProjectInstance create(String name, String owner, String description, List<String> cubes) {
        ProjectInstance projectInstance = new ProjectInstance();

        projectInstance.updateRandomUuid();
        projectInstance.setName(name);
        projectInstance.setOwner(owner);
        projectInstance.setDescription(description);
        projectInstance.setStatus(ProjectStatusEnum.ENABLED);
        projectInstance.setCreateTime(formatTime(System.currentTimeMillis()));
        if (cubes != null)
            projectInstance.setCubes(cubes);
        else
            projectInstance.setCubes(new ArrayList<String>());

        return projectInstance;
    }

    public ProjectInstance() {

    }

    public ProjectInstance(String name, List<String> cubes, String owner) {
        this.name = name;
        this.cubes = cubes;
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ProjectStatusEnum status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean containsCube(String cubeName) {
        return cubes.contains(cubeName.toUpperCase());
    }

    public void removeCube(String cubeName) {
        cubes.remove(cubeName.toUpperCase());
    }

    public int getCubesCount() {
        return cubes.size();
    }

    public void addCube(String cubeName) {
        this.cubes.add(cubeName.toUpperCase());
    }

    public List<String> getCubes() {
        return cubes;
    }


    public void setCubes(List<String> cubes) {
        this.cubes = cubes;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }

    public boolean containsTable(String tableName) {
        return tables.contains(tableName.toUpperCase());
    }

    public void removeTable(String tableName) {
        tables.remove(tableName.toUpperCase());
    }

    public int getTablesCount() {
        return this.getTables().size();
    }

    public void addTable(String tableName) {
        this.getTables().add(tableName.toUpperCase());
    }

    //will return new Set for null
    public Set<String> getTables() {
        tables = tables == null ? new TreeSet<String>() : tables;
        return tables;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void recordUpdateTime(long timeMillis) {
        this.lastUpdateTime = formatTime(timeMillis);
    }

    public List<ProjectDataModel> getDataModels() {
        return dataModels;
    }

    public void setDataModels(List<ProjectDataModel> dataModels) {
        this.dataModels = dataModels;
    }

    public void init() {
        if (name == null)
            name = ProjectInstance.DEFAULT_PROJECT_NAME;

        if (cubes == null) {
            cubes = new ArrayList<String>();
        }

        for (int i = 0; i < cubes.size(); ++i) {
            if (cubes.get(i) != null)
                cubes.set(i, cubes.get(i).toUpperCase());
        }
    }

    public boolean containsRealization(DataModelRealizationType realization, String name) {
        if (dataModels == null) {
            return false;
        }
        for (ProjectDataModel dataModel: dataModels) {
            if (dataModel.getType() == realization && dataModel.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public List<ProjectDataModel> getDataModels(final DataModelRealizationType dataModelRealizationType) {
        return ImmutableList.copyOf(Iterables.filter(dataModels, new Predicate<ProjectDataModel>() {
            @Override
            public boolean apply(@Nullable ProjectDataModel input) {
                return input.getType() == dataModelRealizationType;
            }
        }));
    }

    @Override
    public String toString() {
        return "ProjectDesc [name=" + name + "]";
    }

}
