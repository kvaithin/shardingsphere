/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.sharding.swapper;

import org.apache.shardingsphere.infra.yaml.config.swapper.YamlConfigurationSwapper;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.audit.ShardingAuditStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.keygen.KeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.ShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.rule.YamlTableRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.strategy.audit.YamlShardingAuditStrategyConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.strategy.keygen.YamlKeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.strategy.sharding.YamlShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.sharding.yaml.swapper.rule.ShardingTableRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.sharding.yaml.swapper.strategy.KeyGenerateStrategyConfigurationYamlSwapper;
import org.apache.shardingsphere.sharding.yaml.swapper.strategy.ShardingAuditStrategyConfigurationYamlSwapper;
import org.apache.shardingsphere.sharding.yaml.swapper.strategy.ShardingStrategyConfigurationYamlSwapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class ShardingRuleConfigurationYamlSwapperTest {
    
    @Mock
    private ShardingTableRuleConfigurationYamlSwapper tableRuleConfigurationYamlSwapper;
    
    @Mock
    private ShardingStrategyConfigurationYamlSwapper shardingStrategyConfigurationYamlSwapper;
    
    @Mock
    private KeyGenerateStrategyConfigurationYamlSwapper keyGenerateStrategyConfigurationYamlSwapper;
    
    @Mock
    private ShardingAuditStrategyConfigurationYamlSwapper shardingAuditStrategyConfigurationYamlSwapper;
    
    private final ShardingRuleConfigurationYamlSwapper shardingRuleConfigurationYamlSwapper = new ShardingRuleConfigurationYamlSwapper();
    
    @Before
    public void setUp() throws ReflectiveOperationException {
        setSwapper("tableYamlSwapper", tableRuleConfigurationYamlSwapper);
        when(tableRuleConfigurationYamlSwapper.swapToYamlConfiguration(ArgumentMatchers.any())).thenReturn(mock(YamlTableRuleConfiguration.class));
        when(tableRuleConfigurationYamlSwapper.swapToObject(ArgumentMatchers.any())).thenReturn(mock(ShardingTableRuleConfiguration.class));
        setSwapper("shardingStrategyYamlSwapper", shardingStrategyConfigurationYamlSwapper);
        when(shardingStrategyConfigurationYamlSwapper.swapToYamlConfiguration(ArgumentMatchers.any())).thenReturn(mock(YamlShardingStrategyConfiguration.class));
        when(shardingStrategyConfigurationYamlSwapper.swapToObject(ArgumentMatchers.any())).thenReturn(mock(ShardingStrategyConfiguration.class));
        setSwapper("keyGenerateStrategyYamlSwapper", keyGenerateStrategyConfigurationYamlSwapper);
        when(keyGenerateStrategyConfigurationYamlSwapper.swapToYamlConfiguration(ArgumentMatchers.any())).thenReturn(mock(YamlKeyGenerateStrategyConfiguration.class));
        when(keyGenerateStrategyConfigurationYamlSwapper.swapToObject(ArgumentMatchers.any())).thenReturn(mock(KeyGenerateStrategyConfiguration.class));
        setSwapper("auditStrategyYamlSwapper", shardingAuditStrategyConfigurationYamlSwapper);
        when(shardingAuditStrategyConfigurationYamlSwapper.swapToYamlConfiguration(ArgumentMatchers.any())).thenReturn(mock(YamlShardingAuditStrategyConfiguration.class));
        when(shardingAuditStrategyConfigurationYamlSwapper.swapToObject(ArgumentMatchers.any())).thenReturn(mock(ShardingAuditStrategyConfiguration.class));
    }
    
    private void setSwapper(final String swapperFieldName, final YamlConfigurationSwapper swapperFieldValue) throws ReflectiveOperationException {
        Field field = ShardingRuleConfigurationYamlSwapper.class.getDeclaredField(swapperFieldName);
        field.setAccessible(true);
        field.set(shardingRuleConfigurationYamlSwapper, swapperFieldValue);
    }
    
    @Test
    public void assertSwapToYamlWithMinProperties() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTables().add(mock(ShardingTableRuleConfiguration.class));
        YamlShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swapToYamlConfiguration(shardingRuleConfig);
        assertThat(actual.getTables().size(), is(1));
        assertTrue(actual.getBindingTables().isEmpty());
        assertTrue(actual.getBroadcastTables().isEmpty());
        assertNull(actual.getDefaultDatabaseStrategy());
        assertNull(actual.getDefaultTableStrategy());
        assertNull(actual.getDefaultKeyGenerateStrategy());
        assertNull(actual.getDefaultShardingColumn());
        assertNull(actual.getAuditStrategy());
        assertNull(actual.getScalingName());
        assertTrue(actual.getScaling().isEmpty());
    }
    
    @Test
    public void assertSwapToYamlWithMaxProperties() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTables().add(mock(ShardingTableRuleConfiguration.class));
        shardingRuleConfig.getBindingTableGroups().add("tbl, sub_tbl");
        shardingRuleConfig.getBroadcastTables().add("dict");
        shardingRuleConfig.setDefaultDatabaseShardingStrategy(mock(ShardingStrategyConfiguration.class));
        shardingRuleConfig.setDefaultTableShardingStrategy(mock(ShardingStrategyConfiguration.class));
        shardingRuleConfig.setDefaultTableShardingStrategy(mock(ShardingStrategyConfiguration.class));
        shardingRuleConfig.setDefaultKeyGenerateStrategy(mock(KeyGenerateStrategyConfiguration.class));
        shardingRuleConfig.setAuditStrategy(mock(ShardingAuditStrategyConfiguration.class));
        shardingRuleConfig.setDefaultShardingColumn("user_id");
        YamlShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swapToYamlConfiguration(shardingRuleConfig);
        assertThat(actual.getTables().size(), is(1));
        assertThat(actual.getBindingTables().size(), is(1));
        assertThat(actual.getBindingTables().iterator().next(), is("tbl, sub_tbl"));
        assertThat(actual.getBroadcastTables().size(), is(1));
        assertThat(actual.getBroadcastTables().iterator().next(), is("dict"));
        assertNotNull(actual.getDefaultDatabaseStrategy());
        assertNotNull(actual.getDefaultTableStrategy());
        assertNotNull(actual.getDefaultKeyGenerateStrategy());
        assertNotNull(actual.getAuditStrategy());
        assertThat(actual.getDefaultShardingColumn(), is("user_id"));
    }
    
    @Test
    public void assertSwapToObjectWithMinProperties() {
        YamlShardingRuleConfiguration yamlConfig = new YamlShardingRuleConfiguration();
        yamlConfig.getTables().put("tbl", mock(YamlTableRuleConfiguration.class));
        ShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swapToObject(yamlConfig);
        assertThat(actual.getTables().size(), is(1));
        assertTrue(actual.getBindingTableGroups().isEmpty());
        assertTrue(actual.getBroadcastTables().isEmpty());
        assertNull(actual.getDefaultDatabaseShardingStrategy());
        assertNull(actual.getDefaultTableShardingStrategy());
        assertNull(actual.getDefaultKeyGenerateStrategy());
        assertNull(actual.getAuditStrategy());
        assertNull(actual.getDefaultShardingColumn());
    }
    
    @Test
    public void assertSwapToObjectWithMaxProperties() {
        YamlShardingRuleConfiguration yamlConfig = new YamlShardingRuleConfiguration();
        yamlConfig.getTables().put("tbl", mock(YamlTableRuleConfiguration.class));
        yamlConfig.getBindingTables().add("tbl, sub_tbl");
        yamlConfig.getBroadcastTables().add("dict");
        yamlConfig.setDefaultDatabaseStrategy(mock(YamlShardingStrategyConfiguration.class));
        yamlConfig.setDefaultTableStrategy(mock(YamlShardingStrategyConfiguration.class));
        yamlConfig.setDefaultKeyGenerateStrategy(mock(YamlKeyGenerateStrategyConfiguration.class));
        yamlConfig.setAuditStrategy(mock(YamlShardingAuditStrategyConfiguration.class));
        yamlConfig.setDefaultShardingColumn("user_id");
        ShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swapToObject(yamlConfig);
        assertThat(actual.getTables().size(), is(1));
        assertThat(actual.getBindingTableGroups().size(), is(1));
        assertThat(actual.getBindingTableGroups().iterator().next(), is("tbl, sub_tbl"));
        assertThat(actual.getBroadcastTables().size(), is(1));
        assertThat(actual.getBroadcastTables().iterator().next(), is("dict"));
        assertNotNull(actual.getDefaultDatabaseShardingStrategy());
        assertNotNull(actual.getDefaultTableShardingStrategy());
        assertNotNull(actual.getDefaultKeyGenerateStrategy());
        assertNotNull(actual.getAuditStrategy());
        assertThat(actual.getDefaultShardingColumn(), is("user_id"));
    }
    
    @Test
    public void assertScalingSwap() throws IOException {
        URL url = getClass().getClassLoader().getResource("yaml/sharding-scaling.yaml");
        assertNotNull(url);
        YamlShardingRuleConfiguration yamlConfig = YamlEngine.unmarshal(new File(url.getFile()), YamlShardingRuleConfiguration.class);
        ShardingRuleConfiguration actualConfig = shardingRuleConfigurationYamlSwapper.swapToObject(yamlConfig);
        YamlShardingRuleConfiguration actualYamlConfig = shardingRuleConfigurationYamlSwapper.swapToYamlConfiguration(actualConfig);
        assertThat(YamlEngine.marshal(actualYamlConfig), is(YamlEngine.marshal(yamlConfig)));
    }
}
