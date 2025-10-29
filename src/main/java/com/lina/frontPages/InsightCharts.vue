<script>
import { defineComponent, computed, h } from 'vue'
import { Bar, Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
)

// Bar Chart with Goal Line
export const BarWire = defineComponent({
  name: 'BarWire',
  components: { Bar },
  props: {
    series: { type: Array, required: true },
    goal: { type: Number, default: 140 }
  },
  setup(props) {
    const chartData = computed(() => ({
      labels: props.series.map((_, i) => `${i + 1}일`),
      datasets: [
        {
          label: '일별 실적',
          data: props.series,
          backgroundColor: '#93c5fd',
          borderColor: '#3b82f6',
          borderWidth: 1
        }
      ]
    }))

    const chartOptions = computed(() => ({
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: (context) => `실적: ${context.parsed.y}`
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: '#e5e7eb'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      }
    }))

    return () => h('div', { style: { height: '240px' } }, [
      h(Bar, { data: chartData.value, options: chartOptions.value })
    ])
  }
})

// Year Line Chart
export const YearLine = defineComponent({
  name: 'YearLine',
  components: { Line },
  props: {
    series: { type: Array, required: true }
  },
  setup(props) {
    const chartData = computed(() => ({
      labels: ['2020', '2021', '2022', '2023', '2024'],
      datasets: [
        {
          label: '연도별 성과',
          data: props.series,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4,
          fill: true
        }
      ]
    }))

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        y: {
          beginAtZero: false,
          grid: {
            color: '#e5e7eb'
          }
        }
      }
    }

    return () => h('div', { style: { height: '200px' } }, [
      h(Line, { data: chartData.value, options: chartOptions })
    ])
  }
})

// Month Bars Chart
export const MonthBars = defineComponent({
  name: 'MonthBars',
  components: { Bar },
  props: {
    series: { type: Array, required: true }
  },
  setup(props) {
    const chartData = computed(() => ({
      labels: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
      datasets: [
        {
          label: '월별 성과',
          data: props.series,
          backgroundColor: '#93c5fd',
          borderColor: '#3b82f6',
          borderWidth: 1
        }
      ]
    }))

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: '#e5e7eb'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      }
    }

    return () => h('div', { style: { height: '200px' } }, [
      h(Bar, { data: chartData.value, options: chartOptions })
    ])
  }
})

// Product Series Chart
export const ProductSeries = defineComponent({
  name: 'ProductSeries',
  components: { Line },
  props: {
    series: { type: Object, required: true }
  },
  setup(props) {
    const chartData = computed(() => ({
      labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
      datasets: [
        {
          label: '건강보험',
          data: props.series.A || [],
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4
        },
        {
          label: '종신보험',
          data: props.series.B || [],
          borderColor: '#6b7280',
          backgroundColor: 'rgba(107, 114, 128, 0.1)',
          tension: 0.4,
          borderDash: [5, 5]
        },
        {
          label: '저축보험',
          data: props.series.C || [],
          borderColor: '#16a34a',
          backgroundColor: 'rgba(22, 163, 74, 0.1)',
          tension: 0.4,
          borderDash: [2, 4]
        }
      ]
    }))

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'top'
        }
      },
      scales: {
        y: {
          beginAtZero: false,
          grid: {
            color: '#e5e7eb'
          }
        }
      }
    }

    return () => h('div', { style: { height: '200px' } }, [
      h(Line, { data: chartData.value, options: chartOptions })
    ])
  }
})

// Branch Ranking Chart
export const BranchRanking = defineComponent({
  name: 'BranchRanking',
  components: { Bar },
  props: {
    series: { type: Array, required: true }
  },
  setup(props) {
    const chartData = computed(() => ({
      labels: ['1위', '2위', '3위', '4위', '5위', '6위', '7위', '8위', '9위', '10위'],
      datasets: [
        {
          label: '판매 실적',
          data: props.series.slice(0, 10),
          backgroundColor: '#93c5fd',
          borderColor: '#3b82f6',
          borderWidth: 1
        }
      ]
    }))

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      indexAxis: 'y',
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          beginAtZero: true,
          grid: {
            color: '#e5e7eb'
          }
        },
        y: {
          grid: {
            display: false
          }
        }
      }
    }

    return () => h('div', { style: { height: '280px' } }, [
      h(Bar, { data: chartData.value, options: chartOptions })
    ])
  }
})

</script>
