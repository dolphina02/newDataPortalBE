<template>
  <div class="chubb-data-view">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">Chubb Data</h1>
          <p class="page-subtitle">
            Comprehensive insurance data repository with datasets, dashboards, and reports
          </p>
        </div>
        <div class="header-actions">
          <button class="action-btn primary">
            <IconSystem name="plus" :size="16" />
            New Item
          </button>
          <button class="action-btn">
            <IconSystem name="filter" :size="16" />
            Filter
          </button>
        </div>
      </div>
    </div>

    <!-- Search and Filter -->
    <div class="search-section">
      <div class="search-container">
        <div class="search-box">
          <IconSystem name="search" :size="20" />
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="Search datasets, dashboards, reports..."
            class="search-input"
            @input="handleSearch"
          />
        </div>
        <div class="filter-tabs">
          <button 
            v-for="tab in filterTabs" 
            :key="tab.id"
            class="filter-tab"
            :class="{ active: activeFilter === tab.id }"
            @click="setActiveFilter(tab.id)"
          >
            <IconSystem :name="tab.icon" :size="16" />
            {{ tab.label }}
            <span class="tab-count">{{ getFilteredCount(tab.id) }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Content Grid -->
    <div class="content-section">
      <div class="content-grid">
        <div 
          v-for="item in filteredItems" 
          :key="item.id"
          class="content-card"
          :class="item.type"
          @click="openItem(item)"
        >
          <div class="card-header">
            <div class="card-icon" :class="item.type">
              <IconSystem :name="getTypeIcon(item.type)" :size="24" />
            </div>
            <div class="card-meta">
              <span class="card-type">{{ item.type.toUpperCase() }}</span>
              <span class="card-updated">{{ item.updated }}</span>
            </div>
          </div>
          
          <div class="card-content">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-description">{{ item.description }}</p>
            
            <div class="card-tags">
              <span 
                v-for="tag in item.tags" 
                :key="tag"
                class="tag"
                :class="{ highlighted: searchQuery && tag.toLowerCase().includes(searchQuery.toLowerCase()) }"
              >
                {{ tag }}
              </span>
            </div>
          </div>
          
          <div class="card-footer">
            <div class="card-stats">
              <div class="stat">
                <IconSystem name="eye" :size="14" />
                <span>{{ item.views }}</span>
              </div>
              <div class="stat" v-if="item.downloads">
                <IconSystem name="download" :size="14" />
                <span>{{ item.downloads }}</span>
              </div>
              <div class="stat" v-if="item.queries">
                <IconSystem name="activity" :size="14" />
                <span>{{ item.queries }}</span>
              </div>
            </div>
            <div class="card-actions">
              <button class="card-action-btn" @click.stop="favoriteItem(item)" :class="{ favorited: item.favorited }">
                <IconSystem name="heart" :size="16" />
              </button>
              <button class="card-action-btn" @click.stop="shareItem(item)">
                <IconSystem name="share" :size="16" />
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-if="filteredItems.length === 0" class="empty-state">
        <IconSystem name="search" :size="64" />
        <h3>No items found</h3>
        <p v-if="searchQuery">No results for "{{ searchQuery }}"</p>
        <p v-else>No {{ activeFilter === 'all' ? 'items' : activeFilter }} available</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import IconSystem from './IconSystem.vue'

const searchQuery = ref('')
const activeFilter = ref('all')

const filterTabs = [
  { id: 'all', label: 'All', icon: 'grid' },
  { id: 'dataset', label: 'Datasets', icon: 'database' },
  { id: 'dashboard', label: 'Dashboards', icon: 'activity' },
  { id: 'report', label: 'Reports', icon: 'file-text' }
]

// Sample Chubb insurance data
const chubbData = [
  // Datasets
  {
    id: 1,
    type: 'dataset',
    title: 'Property Claims Database',
    description: 'Comprehensive property insurance claims data including residential and commercial properties with loss details, coverage information, and settlement outcomes.',
    tags: ['property', 'claims', 'residential', 'commercial'],
    views: '2.4k',
    downloads: '156',
    updated: '2 hours ago',
    favorited: false
  },
  {
    id: 2,
    type: 'dataset',
    title: 'Cyber Risk Assessment Data',
    description: 'Cybersecurity incident data and risk assessment metrics for enterprise clients, including threat vectors, impact analysis, and mitigation strategies.',
    tags: ['cyber', 'risk', 'security', 'enterprise'],
    views: '1.8k',
    downloads: '89',
    updated: '5 hours ago',
    favorited: true
  },
  {
    id: 3,
    type: 'dataset',
    title: 'Marine Insurance Portfolio',
    description: 'Global marine insurance data covering cargo, hull, and liability coverage with shipping routes, vessel information, and claim histories.',
    tags: ['marine', 'cargo', 'shipping', 'liability'],
    views: '1.2k',
    downloads: '67',
    updated: '1 day ago',
    favorited: false
  },
  
  // Dashboards
  {
    id: 4,
    type: 'dashboard',
    title: 'Claims Performance Analytics',
    description: 'Real-time dashboard monitoring claims processing efficiency, settlement ratios, and customer satisfaction metrics across all product lines.',
    tags: ['claims', 'performance', 'analytics', 'kpi'],
    views: '3.1k',
    queries: '245',
    updated: '30 minutes ago',
    favorited: true
  },
  {
    id: 5,
    type: 'dashboard',
    title: 'Underwriting Risk Monitor',
    description: 'Comprehensive risk assessment dashboard showing exposure analysis, pricing models, and portfolio performance by geography and product type.',
    tags: ['underwriting', 'risk', 'pricing', 'portfolio'],
    views: '2.7k',
    queries: '189',
    updated: '1 hour ago',
    favorited: false
  },
  {
    id: 6,
    type: 'dashboard',
    title: 'Cyber Threat Intelligence',
    description: 'Advanced cybersecurity dashboard displaying threat landscape, incident trends, and risk exposure for cyber insurance products.',
    tags: ['cyber', 'threat', 'intelligence', 'security'],
    views: '1.9k',
    queries: '134',
    updated: '3 hours ago',
    favorited: false
  },
  
  // Reports
  {
    id: 7,
    type: 'report',
    title: 'Q1 2025 Property Loss Report',
    description: 'Quarterly analysis of property insurance losses including catastrophic events, frequency trends, and regional impact assessment.',
    tags: ['quarterly', 'property', 'loss', 'catastrophic'],
    views: '4.2k',
    updated: '2 days ago',
    favorited: true
  },
  {
    id: 8,
    type: 'report',
    title: 'Cyber Insurance Market Analysis',
    description: 'Comprehensive market research report on cyber insurance trends, emerging risks, and competitive landscape analysis.',
    tags: ['market', 'cyber', 'trends', 'competitive'],
    views: '3.5k',
    updated: '3 days ago',
    favorited: false
  },
  {
    id: 9,
    type: 'report',
    title: 'Marine Claims Severity Study',
    description: 'In-depth analysis of marine insurance claim severity patterns, including cargo damage, vessel incidents, and environmental factors.',
    tags: ['marine', 'severity', 'cargo', 'environmental'],
    views: '2.1k',
    updated: '1 week ago',
    favorited: false
  },
  {
    id: 10,
    type: 'dataset',
    title: 'Commercial Auto Fleet Data',
    description: 'Fleet insurance data including vehicle specifications, driver profiles, accident history, and telematics information for commercial vehicles.',
    tags: ['commercial', 'auto', 'fleet', 'telematics'],
    views: '1.6k',
    downloads: '92',
    updated: '4 hours ago',
    favorited: false
  },
  {
    id: 11,
    type: 'dashboard',
    title: 'Regulatory Compliance Monitor',
    description: 'Compliance tracking dashboard for insurance regulations across multiple jurisdictions with automated reporting and alert systems.',
    tags: ['compliance', 'regulatory', 'reporting', 'alerts'],
    views: '2.3k',
    queries: '167',
    updated: '6 hours ago',
    favorited: true
  },
  {
    id: 12,
    type: 'report',
    title: 'ESG Impact Assessment',
    description: 'Environmental, Social, and Governance impact analysis for insurance operations and investment portfolios with sustainability metrics.',
    tags: ['esg', 'sustainability', 'environmental', 'governance'],
    views: '1.7k',
    updated: '5 days ago',
    favorited: false
  }
]

const filteredItems = computed(() => {
  let items = chubbData
  
  // Filter by type
  if (activeFilter.value !== 'all') {
    items = items.filter(item => item.type === activeFilter.value)
  }
  
  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    items = items.filter(item => 
      item.title.toLowerCase().includes(query) ||
      item.description.toLowerCase().includes(query) ||
      item.tags.some(tag => tag.toLowerCase().includes(query))
    )
  }
  
  return items
})

const getFilteredCount = (filterId) => {
  if (filterId === 'all') {
    return chubbData.length
  }
  return chubbData.filter(item => item.type === filterId).length
}

const setActiveFilter = (filterId) => {
  activeFilter.value = filterId
}

const getTypeIcon = (type) => {
  const icons = {
    dataset: 'database',
    dashboard: 'activity',
    report: 'file-text'
  }
  return icons[type] || 'file'
}

const handleSearch = () => {
  // Search is reactive through computed property
}

const openItem = (item) => {
  console.log('Opening item:', item.title)
  // Handle item opening logic
}

const favoriteItem = (item) => {
  item.favorited = !item.favorited
  console.log('Favorited:', item.title, item.favorited)
}

const shareItem = (item) => {
  console.log('Sharing item:', item.title)
  // Handle sharing logic
}
</script>

<style scoped>
.chubb-data-view {
  display: flex;
  flex-direction: column;
  padding: var(--space-4);
  gap: var(--space-4);
}

/* Search Section */
.search-section {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-6);
}

.search-container {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.search-box {
  display: flex;
  align-items: center;
  background: var(--bg-primary);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  padding: var(--space-3);
  gap: var(--space-3);
  max-width: 500px;
}

.search-box:focus-within {
  border-color: var(--lina-yellow);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--lina-yellow) 10%, transparent);
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: var(--text-primary);
  font-size: var(--fs-base);
}

.search-input::placeholder {
  color: var(--text-tertiary);
}

.filter-tabs {
  display: flex;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-lg);
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: var(--transition-fast);
  font-size: var(--fs-sm);
  font-weight: var(--fw-medium);
}

.filter-tab:hover {
  background: var(--surface-hover);
  border-color: var(--lina-yellow);
  color: var(--text-primary);
}

.filter-tab.active {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.tab-count {
  background: color-mix(in srgb, var(--text-secondary) 10%, transparent);
  color: var(--text-secondary);
  font-size: var(--fs-xs);
  font-weight: var(--fw-bold);
  padding: 2px 6px;
  border-radius: var(--radius-full);
  min-width: 20px;
  text-align: center;
}

.filter-tab.active .tab-count {
  background: color-mix(in srgb, var(--gray-800) 15%, transparent);
  color: var(--gray-800);
}

/* Content Grid */
.content-section {
  min-height: 400px;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: var(--space-6);
}

.content-card {
  background: var(--surface);
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  cursor: pointer;
  transition: var(--transition-fast);
  display: flex;
  flex-direction: column;
}

.content-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--lina-yellow);
}

.content-card.dataset {
  border-left: 4px solid var(--info);
}

.content-card.dashboard {
  border-left: 4px solid var(--lina-orange);
}

.content-card.report {
  border-left: 4px solid var(--success);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: var(--space-5);
  background: var(--surface-hover);
  border-bottom: 1px solid var(--border-primary);
}

.card-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
}

.card-icon.dataset {
  background: color-mix(in srgb, var(--info) 10%, transparent);
  color: var(--info);
}

.card-icon.dashboard {
  background: color-mix(in srgb, var(--lina-orange) 10%, transparent);
  color: var(--lina-orange);
}

.card-icon.report {
  background: color-mix(in srgb, var(--success) 10%, transparent);
  color: var(--success);
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: var(--space-1);
}

.card-type {
  font-size: var(--fs-xs);
  font-weight: var(--fw-bold);
  color: var(--text-tertiary);
  letter-spacing: 0.05em;
}

.card-updated {
  font-size: var(--fs-xs);
  color: var(--text-tertiary);
}

.card-content {
  padding: var(--space-5);
  flex: 1;
}

.card-title {
  font-size: var(--fs-lg);
  font-weight: var(--fw-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-3) 0;
  line-height: var(--lh-snug);
}

.card-description {
  color: var(--text-secondary);
  margin: 0 0 var(--space-4) 0;
  line-height: var(--lh-relaxed);
  font-size: var(--fs-sm);
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.tag {
  background: var(--surface-active);
  color: var(--text-secondary);
  font-size: var(--fs-xs);
  font-weight: var(--fw-medium);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-secondary);
}

.tag.highlighted {
  background: var(--lina-yellow);
  color: var(--gray-800);
  border-color: var(--lina-yellow);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) var(--space-5);
  background: var(--bg-primary);
  border-top: 1px solid var(--border-primary);
}

.card-stats {
  display: flex;
  gap: var(--space-4);
}

.stat {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  color: var(--text-tertiary);
  font-size: var(--fs-xs);
}

.card-actions {
  display: flex;
  gap: var(--space-2);
}

.card-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: var(--surface-hover);
  border-radius: var(--radius-md);
  color: var(--text-tertiary);
  cursor: pointer;
  transition: var(--transition-fast);
}

.card-action-btn:hover {
  background: var(--surface-active);
  color: var(--text-primary);
}

.card-action-btn.favorited {
  color: var(--error);
}

.card-action-btn.favorited:hover {
  color: var(--error);
  background: color-mix(in srgb, var(--error) 10%, transparent);
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-16);
  text-align: center;
  color: var(--text-tertiary);
}

.empty-state h3 {
  font-size: var(--fs-xl);
  font-weight: var(--fw-semibold);
  color: var(--text-secondary);
  margin: var(--space-4) 0 var(--space-2) 0;
}

.empty-state p {
  font-size: var(--fs-base);
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-container {
    gap: var(--space-3);
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-tabs {
    overflow-x: auto;
    padding-bottom: var(--space-2);
  }
}
</style>