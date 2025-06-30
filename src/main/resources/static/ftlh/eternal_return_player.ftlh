<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="/css/eternal_return_record.css">
    <script src="/javascript/chart.js"></script>
</head>
<body>
<div id="content-container">
    <div id="header">
        <div id="banner_user_info">
            <div class="profile-image-wrapper">
                <#if profileImageUrl??>
                    <img src="${profileImageUrl}" alt=""/>
                <#else>
                    <#include "eternal_return_error_img.ftlh">
                </#if>
            </div>
            <div id="top">
                <div class="level">Lv.${level}</div>
                <div class="nickname">${nickName}</div>
                <#if playTime == 0>
                    <p>当前赛季游戏时间： 0小时0分钟</p>
                <#else>
                    <p>当前赛季游戏时间： ${(playTime/60/60)?floor}小时${playTime%60}分钟</p>
                </#if>
            </div>
        </div>
        <div id="describe">
            <div id="logo">Design inspired by DakGG •
                Powered by LoMu-Bot</div>
        </div>
    </div>
    <div id="body">
        <div id="left">
            <div id="rank">
                <h4>排位(${season})</h4>
                <div id="score">
                    <div id="rp_img">
                        <#if data.tierImageUrl == "">
                            <img src="/images/eternal_return/tier/0" alt="">
                        <#else>
                            <img src="${data.tierImageUrl}" alt="">
                        </#if>
                    </div>
                    <div id="rp_box">
                        <div id="rp">
                            ${data.rp}
                        </div>
                        <div id="rp_name">
                            ${data.rpName}
                        </div>
                    </div>
                </div>
                <div id="record">
                    <div>
                        <div class="record_box">
                            <h4>平均TK</h4>
                            <h4>${data.avgTk}</h4>
                        </div>
                        <div class="record_box">
                            <h4>TOP 1</h4>
                            <h4>${data.top1}</h4>
                        </div>
                        <div class="record_box">
                            <h4>游戏场次</h4>
                            <h4>${data.play}</h4>
                        </div>

                        <div class="record_box">
                            <h4>平均击杀</h4>
                            <h4>${data.avgKill}</h4>
                        </div>
                        <div class="record_box">
                            <h4>TOP 2</h4>
                            <h4>${data.top2}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均伤害</h4>
                            <h4>${data.avgDmg}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均助攻</h4>
                            <h4>${data.avgAssists}</h4>
                        </div>
                        <div class="record_box">
                            <h4>TOP 3</h4>
                            <h4>${data.top3}</h4>
                        </div>
                        <div class="record_box">
                            <h4>平均排名</h4>
                            <h4>${data.avgRank}</h4>
                        </div>
                    </div>
                </div>

                <#if mmrStats??>
                    <div id="rank_stats">
                        <canvas id="rank_canvas"></canvas>
                    </div>
                    <script>
                        const ctx = document.getElementById('rank_canvas');
                        const labels = ${mmrStats.mmrDateJson?no_esc};
                        const data = {
                            labels: labels,
                            datasets: [{
                                data: ${mmrStats.mmrJson},
                                fill: false,
                                borderColor: 'rgb(202, 164, 40)',
                                backgroundColor: ['rgb(202, 164, 40)'],
                                pointRadius: 4,
                                tension: 0.1,
                            }]
                        };

                        const config = {
                            type: 'line', // 表类型
                            data: data,
                            options: {
                                plugins: {
                                    legend: {
                                        display: false
                                    }
                                },
                                scales: {
                                    x: {
                                        grid: {
                                            display: false
                                        }
                                    },
                                    y: {
                                        grid: {
                                            display: false
                                        },
                                        ticks: {
                                            stepSize: 100
                                        }
                                    }
                                }
                            }
                        };
                        const myChart = new Chart(ctx, config);
                    </script>
                </#if>


            </div>

            <section>
                <table id="rank_character_stats">
                    <thead>
                    <tr>
                        <th class="character">角色</th>
                        <th class="win-rate">胜率</th>
                        <th class="get-rp">RP</th>
                        <th class="avg-rank">平均排名</th>
                        <th class="avg-dmg">平均伤害</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list characterUseStats as character>
                        <tr>
                            <td class="character">
                                <div class="image-wrapper"><img
                                            src="${character.imgUrl}"
                                            alt=""></div>
                                <div class="info">${character.characterName}
                                    <div class="plays">${character.characterPlay} 游戏</div>
                                </div>
                            </td>
                            <td class="win-rate">${character.winRate}</td>
                            <td class="get-rp">
                                <#if character.getRP gte 0>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="8" height="5" viewBox="0 0 8 5"
                                         fill="none"
                                         style="transform: none;">
                                        <path d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                                              fill="#FF4655"></path>
                                    </svg>
                                    ${character.getRP}
                                <#else>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="8" height="5" viewBox="0 0 8 5"
                                         fill="none" style="transform: rotate(180deg);">
                                        <path
                                                d="M6.75 4.75C7.17188 4.75 7.38281 4.25781 7.07812 3.95312L4.07812 0.953125C3.89062 0.765625 3.58594 0.765625 3.39844 0.953125L0.398438 3.95312C0.09375 4.25781 0.304688 4.75 0.726562 4.75H6.75Z"
                                                fill="#5393ca"></path>
                                    </svg>
                                    ${character.getRP}
                                </#if>
                            </td>
                            <td class="avg-rank">${character.avgRank}</td>
                            <td class="avg-dmg">${character.avgDmg}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </section>
            <section>
                <table id="recent_play">
                    <thead>
                    <tr>
                        <th class="character">一起游戏的玩家 (最近组排队友)</th>
                        <th class="win-rate">胜率</th>
                        <th class="avg-rank">平均排名</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list recentPlayers as recentPlayer>
                        <tr>
                            <td class="character">
                                <div class="image-wrapper"><img
                                            src="${recentPlayer.imageWrapperUrl}"
                                            alt=""></div>
                                <div class="info">${recentPlayer.nickname}
                                    <div class="plays">${recentPlayer.plays} 游戏</div>
                                </div>
                            </td>
                            <td class="win-rate">
                                ${recentPlayer.winRate}
                            </td>
                            <td class="avg-rank">
                                ${recentPlayer.avgRank}
                            </td>
                        </tr>
                    </#list>

                    </tbody>
                </table>
            </section>
        </div>
        <div id="right">
            <#if lomuRating??>
                <div id="lomu_rating">
                    ${lomuRating?no_esc}
                </div>
            </#if>
            ${rightContent?no_esc}
        </div>
    </div>
</div>
</body>

</html>