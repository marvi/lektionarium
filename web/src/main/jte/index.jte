@param lectio.web.DayView day
@param java.util.List<Integer> years
@param String feedUrl
@param String feedWebcal
@template.layout(
  title = day.name() + " – Lektionarium",
  years = years,
  feedUrl = feedUrl,
  feedWebcal = feedWebcal,
  content = @`
    @template.dagkort(day = day)
  `)
