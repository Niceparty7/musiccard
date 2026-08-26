<script setup>
	import {
		onMounted,
		ref
	} from 'vue';
	import AlbumArtwork from '../../components/AlbumArtwork.vue';
	import {
		consoleApi
	} from '../../api/console';
	const stats = ref({
			yearCount: 0,
			monthCount: 0,
			dayCount: 0
		}),
		recent = ref([]);
	onMounted(async () => {
		const [s, m] = await Promise.all([consoleApi.statistics(), consoleApi.musicList({
			page: 1
		})]);
		stats.value = s.result;
		recent.value = (m.result.list || []).slice(0, 5)
	})
</script>
<template>
	<div>
		<div class="heading">
			<div><span class="eyebrow">SYSTEM OVERVIEW / LIVE</span>
				<h1 class="page-title">声场总览</h1>
			</div>
			<div class="health"><i></i><span>所有服务运行正常<small>最后检查：刚刚</small></span></div>
		</div>
		<section class="stat-grid">
			<article class="primary"><span>音乐总资产</span><b>{{stats.yearCount.toLocaleString()}}</b><small>2026
					累计收录</small>
				<div class="spark"><i v-for="n in 18" :key="n" :style="{height:(22+(n*29%70))+'%'}"></i></div>
			</article>
			<article><span>本月新增</span><b>{{stats.monthCount}}</b><small>MONTH / {{stats.month}}</small><em>+12.8%</em>
			</article>
			<article><span>今日入库</span><b>{{stats.dayCount}}</b><small>DAY / {{stats.day}}</small><em
					class="cyan">实时</em></article>
			<article class="orbit-card"><span>内容完整度</span>
				<div class="progress"><b>92</b><small>%</small></div>
				<p>168 条内容待优化</p>
			</article>
		</section>
		<section class="dashboard-grid">
			<article class="panel">
				<div class="panel-head">
					<div><span class="eyebrow">LATEST SIGNALS</span>
						<h2>最近入库</h2>
					</div><router-link to="/console/music">查看全部 →</router-link>
				</div>
				<div class="recent">
					<div v-for="item in recent" :key="item.id">
						<AlbumArtwork :item="item" /><span><b>{{item.musicName}}</b><small>{{item.singerName}} ·
								{{item.typeName}}</small></span><em>已发布</em><i>↗</i>
					</div>
				</div>
			</article>
			<article class="panel pulse">
				<div class="panel-head">
					<div><span class="eyebrow">ACTIVITY MAP</span>
						<h2>内容脉冲</h2>
					</div><span>近 7 日</span>
				</div>
				<div class="chart">
					<div v-for="(v,i) in [42,68,54,82,64,92,76]" :key="i"><i
							:style="{height:v+'%'}"></i><small>{{['一','二','三','四','五','六','日'][i]}}</small></div>
				</div>
				<div class="legend"><span><i></i>新增音乐</span><b>+286</b></div>
			</article>
		</section>
	</div>
</template>
<style scoped>
	.heading {
		display: flex;
		align-items: end;
		justify-content: space-between;
		margin-bottom: 30px
	}

	.health {
		display: flex;
		align-items: center;
		gap: 10px;
		padding: 12px 15px;
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 14px;
		font-size: 11px
	}

	.health>i {
		width: 9px;
		height: 9px;
		border-radius: 50%;
		background: var(--lime);
		box-shadow: 0 0 10px var(--lime)
	}

	.health span {
		display: grid;
		font-weight: 700
	}

	.health small {
		color: var(--muted);
		font-weight: 400
	}

	.stat-grid {
		display: grid;
		grid-template-columns: 1.4fr 1fr 1fr 1.1fr;
		gap: 16px
	}

	.stat-grid article {
		position: relative;
		min-height: 178px;
		padding: 22px;
		border: 1px solid var(--line);
		border-radius: 20px;
		background: #fff;
		overflow: hidden
	}

	.stat-grid article>span {
		font-size: 12px;
		color: var(--muted)
	}

	.stat-grid article>b {
		display: block;
		margin-top: 18px;
		font: 47px var(--display);
		letter-spacing: -.05em
	}

	.stat-grid article>small {
		font: 9px var(--mono);
		letter-spacing: .12em;
		color: #9aa9b9
	}

	.stat-grid .primary {
		background: var(--ink-950);
		color: #fff
	}

	.primary .spark {
		position: absolute;
		display: flex;
		align-items: end;
		gap: 4px;
		right: 18px;
		top: 20px;
		bottom: 20px;
		width: 42%
	}

	.spark i {
		flex: 1;
		background: linear-gradient(var(--cyan), rgba(48, 217, 255, .05));
		border-radius: 3px
	}

	.stat-grid em {
		position: absolute;
		right: 18px;
		bottom: 18px;
		padding: 5px 8px;
		border-radius: 99px;
		background: #eff9e3;
		color: #609812;
		font: 9px var(--mono);
		font-style: normal
	}

	.stat-grid em.cyan {
		background: #e4faff;
		color: #0087a5
	}

	.orbit-card {
		background: linear-gradient(145deg, #fff, #eef1ff) !important
	}

	.progress {
		position: absolute;
		right: -16px;
		top: -20px;
		width: 130px;
		height: 130px;
		border: 12px solid rgba(124, 108, 255, .16);
		border-top-color: var(--violet);
		border-radius: 50%;
		display: flex;
		align-items: end;
		justify-content: center;
		padding-bottom: 25px
	}

	.progress b {
		font: 32px var(--display)
	}

	.progress small {
		font: 10px var(--mono)
	}

	.orbit-card p {
		position: absolute;
		bottom: 12px;
		font-size: 10px;
		color: var(--muted)
	}

	.dashboard-grid {
		display: grid;
		grid-template-columns: 1.35fr .65fr;
		gap: 16px;
		margin-top: 16px
	}

	.panel {
		padding: 23px;
		background: #fff;
		border: 1px solid var(--line);
		border-radius: 20px
	}

	.panel-head {
		display: flex;
		justify-content: space-between;
		align-items: start;
		margin-bottom: 18px
	}

	.panel-head h2 {
		margin: 5px 0 0;
		font: 25px var(--display)
	}

	.panel-head>a,
	.panel-head>span {
		font-size: 10px;
		color: var(--muted)
	}

	.recent>div {
		display: grid;
		grid-template-columns: 56px 1fr auto 22px;
		align-items: center;
		gap: 12px;
		padding: 10px 0;
		border-top: 1px solid var(--line)
	}

	.recent :deep(.art) {
		min-height: 48px;
		border-radius: 10px
	}

	.recent span {
		display: grid
	}

	.recent b {
		font-size: 12px
	}

	.recent small {
		font-size: 9px;
		color: var(--muted)
	}

	.recent em {
		font-size: 9px;
		color: #4f8b00;
		background: #eff9e3;
		border-radius: 99px;
		padding: 4px 7px;
		font-style: normal
	}

	.chart {
		height: 210px;
		display: flex;
		align-items: end;
		gap: 10px;
		padding-top: 20px
	}

	.chart>div {
		height: 100%;
		display: flex;
		flex: 1;
		flex-direction: column;
		justify-content: end;
		align-items: center;
		gap: 7px
	}

	.chart i {
		width: 100%;
		max-width: 26px;
		background: linear-gradient(var(--violet), #c9c4ff);
		border-radius: 6px 6px 2px 2px
	}

	.chart small {
		font-size: 9px;
		color: var(--muted)
	}

	.legend {
		display: flex;
		justify-content: space-between;
		border-top: 1px solid var(--line);
		padding-top: 14px;
		font-size: 10px
	}

	.legend span i {
		display: inline-block;
		width: 7px;
		height: 7px;
		border-radius: 2px;
		background: var(--violet);
		margin-right: 6px
	}

	@media(max-width:1100px) {
		.stat-grid {
			grid-template-columns: repeat(2, 1fr)
		}

		.dashboard-grid {
			grid-template-columns: 1fr
		}
	}

	@media(max-width:600px) {
		.heading {
			align-items: start
		}

		.health {
			display: none
		}

		.stat-grid {
			grid-template-columns: 1fr 1fr
		}

		.stat-grid article {
			min-height: 145px;
			padding: 17px
		}

		.stat-grid article>b {
			font-size: 38px
		}

		.primary {
			grid-column: span 2
		}

		.dashboard-grid {
			display: block
		}

		.pulse {
			margin-top: 16px
		}
	}
</style>