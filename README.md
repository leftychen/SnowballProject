1.架构设计

整体框架分为两部分，一个是衍生品架构设计，另一个是模型架构设计
衍生品架构Base Class 是 Derivative， 然后sub class会是Option，Swap， Futures以及其他衍生品。Option class往下可以继续增加其他option，或者option组合，比如Snowball， Asian或者Bull call spread。 于此同时，PayoffScripts interface 可以方便不同的option 添加其独有的payoff， 以方便使用Monte Carlo Simulation。 

模型架构Base Class是Model， 然后sub class 是ClosedForm model 和 Monte Carlo Sim Model。 Greeks 的计算方式在这两个classes 里定义。因为并不是所有模型可以像BS model一样有相应的Greeks 公式。所以Greeks 计算方式是
(price_up-price_dn)/(2*shockRate)

ClosedForm model以及Monte Carlo Simulation model 还可以继续扩展。比如说，添加Black Scholes Model或者TurnBull Wakeman model， 又或者根据产品需要设计使用model with fix vol或者vol surface。
Monte Carlo Simulation model可以扩展增加Brownian motion sim，或者Heston model sim。 最后生成的path方便pricing option。 

2.Snowball option Monte Carlo Simulation 开发简述

首先根据snowball 交易日spot level确定knockout 和 knockin 的level是多少。之后生成未来9个 reset dates。（观察期是3个月）用来比对是否期权会被knockout。
之后根据市场情况生成10000条路径。我使用Apache math lib里的matrix生成一个10000 x 365的矩阵，当sim 未来每一天时，使用向量运算， 这样只需要O(n)就可以完成整体模拟。

当路径生成完毕，loop每一天和每一条路径的index 是否符合如下规则：


1）	如果此路径index 在reset date时大于等于 knockout index level， 则期权被knockout， payoff 为 年化收益率 x 计息天数/365，并且discount 到今天（假设名义本金是1， 并且discount 根据knock date拒今天的距离）

2）	如果此路径 index在任何模拟日时小于等于knockin index level并且此路径还没有被knockout， 此路径则被定义为knockin。

在模拟的最后一天，计算所有被knockin的路径收益率。公式为end index level / 开始时的 indexlevel – 1，并discount 到今天。那些既没有knockin，也没有knockout到路径，计算公式是为 年化收益率 x 360/365，并且discount 到今天。

最后每条路径的payoff平均值，则为此snowball option的价格。

3.分析
如果假设，中证500的年化收益率为24.5%，波动率为15.1%，risk free rate为1%， 分红率为8.9%和6226.42作为交易日指数，估值1年期snowball option：knockIn为80%， knockout为100%，年化收益率为16.6%，10000 条Monte Carlo sim price为14%，delta为0.17. 
经过观察，91%的路径会被knockout， 2%的路径会被knockin，7%的路径会平缓的走完整个期权存续期。

4.改进 

首先市场数据（indexlevel，index return，volatility…）可以封装到对象里，并更在对象里进行加工。这样方便与数据库连接并在其他对象里使用。同时也可以减少在Model class和 Derivative class 里function overloading。 

我并没有深入了解Apache matrix的内部构件，可能在向量计算时，其内部方法并不能有效的减少运算时间，所以需要更多的研究。并且，这个matrix可加载的运算方式有限（比如不能对向量使用exp），所以需要寻找更好的数学lib或者使用其他语言的计算来增加运算效率。

在检查每条路径的index 是否符合knockin和knockout条件时，我使用的是double for loop，运算时间是O（n2）。如果有更方便的矩阵运算方式，可以将此处的big O减少至少到O（n）



